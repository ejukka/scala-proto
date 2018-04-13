package proto.scalaproto

import cats.effect.IO
import fs2.StreamApp
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global

object HelloWorldServer extends StreamApp[IO] with Http4sDsl[IO] {
  val service@cats.data.Kleisli(run) = HttpService[IO] {
    case GET -> Root / "hello" / name =>
      Ok(getJson(s"Hello, ${name}"))
    case GET -> Root / "test" =>
      Ok(s"just testing")
    case _ => NoContent()
  }

  private def getJson(value: String): Json = {
    Json.obj("message" -> Json.fromString(value))
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}
