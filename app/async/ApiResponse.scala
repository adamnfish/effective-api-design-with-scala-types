package async

import models.{ApiError, ApiErrors}
import play.api.libs.json._
import play.api.mvc.{Result, Results}

import scala.concurrent.{ExecutionContext, Future}


case class ApiResponse[A] private (underlying: Future[Either[ApiErrors, A]]) {
  def map[B](f: A => B)(implicit ec: ExecutionContext): ApiResponse[B] =
    flatMap(a => ApiResponse.Right(f(a)))

  def flatMap[B](f: A => ApiResponse[B])(implicit ec: ExecutionContext): ApiResponse[B] = ApiResponse {
    asFuture.flatMap {
      case Right(a) => f(a).asFuture
      case Left(e) => Future.successful(Left(e))
    }
  }

  def fold[B](failure: ApiErrors => B, success: A => B)(implicit ec: ExecutionContext): Future[B] = {
    asFuture.map(_.fold(failure, success))
  }

  /**
   * If there is an error in the Future itself (e.g. a timeout) we convert it to a
   * Left so we have a consistent error representation. This would likely have
   * logging around it, or you may have an error representation that carries more info
   * for these kinds of issues.
   */
  def asFuture(implicit ec: ExecutionContext): Future[Either[ApiErrors, A]] = {
    underlying recover { case err =>
      val apiErrors = ApiErrors(List(ApiError.unexpected(err.getMessage)))
      scala.Left(apiErrors)
    }
  }
}

object ApiResponse extends Results {
  /**
   * Create an ApiResponse instance from a "good" value.
   */
  def Right[A](a: A): ApiResponse[A] =
    ApiResponse(Future.successful(scala.Right(a)))

  /**
   * Create an ApiResponse failure from an ApiErrors instance.
   */
  def Left[A](err: ApiErrors): ApiResponse[A] =
    ApiResponse(Future.successful(scala.Left(err)))

  /**
   * Asyncronous versions of the ApiResponse Right/Left helpers for when you have
   * a Future that returns a good/bad value directly.
   */
  object Async {
    /**
     * Create an ApiResponse from a Future of a good value.
     */
    def Right[A](fa: Future[A])(implicit ec: ExecutionContext): ApiResponse[A] =
      ApiResponse(fa.map(scala.Right(_)))

    /**
     * Create an ApiResponse from a known failure in the future. For example,
     * if a piece of logic fails but you need to make a Database/API call to
     * get the failure information.
     */
    def Left[A](ferr: Future[ApiErrors])(implicit ec: ExecutionContext): ApiResponse[A] =
      ApiResponse(ferr.map(scala.Left(_)))
  }

  def apply[T](action: => ApiResponse[T])(implicit tjs: Writes[T], ec: ExecutionContext): Future[Result] = {
    action.fold(
      err =>
        Status(err.statusCode) {
          JsObject(Seq(
            "status" -> JsString("error"),
            "statusCode" -> JsNumber(err.statusCode),
            "errors" -> Json.toJson(err.errors)
          ))
        },
      t =>
        Ok {
          JsObject(Seq(
            "status" -> JsString("ok"),
            "response" -> Json.toJson(t)
          ))
        }
    )
  }
}
