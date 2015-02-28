package example

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._
import unfiltered.netty.websockets._
import scalaz.stream.Process
import scalaz.concurrent.{Task,Strategy}
import scala.concurrent.duration._
import java.util.concurrent.{Executors, ExecutorService, ScheduledExecutorService, ThreadFactory}

object Server {

  val serverPool: ExecutorService =
    Executors.newCachedThreadPool(daemonThreads("server"))

  val schedulingPool: ScheduledExecutorService =
    Executors.newScheduledThreadPool(2, daemonThreads("scheduled-tasks"))

  private def daemonThreads(name: String) = new ThreadFactory {
    def newThread(r: Runnable) = {
      val t = Executors.defaultThreadFactory.newThread(r)
      t.setDaemon(true)
      t.setName(name)
      t
    }
  }

  val stream: Process[Task,Long] =
    Process.awakeEvery(1.seconds)(Strategy.Executor(serverPool),schedulingPool)
           .map(_ => System.currentTimeMillis)

  def main(args: Array[String]): Unit = {
    unfiltered.netty.Server
      .http(8080)
      .resources(getClass.getResource("/www/"), cacheSeconds = 60)
      .handler(Example(stream))
      .run
  }
}

@io.netty.channel.ChannelHandler.Sharable
case class Example(proc: Process[Task,Long]) extends websockets.Plan with cycle.SynchronousExecution with ServerErrorResponse {
  println(">>>>>>>>>>>>>>>>>>>>>>")

  val pass: PassHandler = DefaultPassHandler
  def intent = {
    case GET(Path("/stream")) => {
      case Open(s) => proc.evalMap(long => s.send(long.toString))
      // case Message(s, Text(str)) => sockets.foreach(_.send(str.reverse))
      // case Close(s)              => sockets -= s
      case Error(s, e)           => println("error %s" format e.getMessage)
    }
  }
}
