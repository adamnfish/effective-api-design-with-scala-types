package sync

import play.api.libs.json._
import play.api.mvc.{Result, Results}


object ApiResponse extends Results {
  def apply[T](action: => ApiResponse[T])(implicit tjs: Writes[T]): Result = {
    action.fold(
      apiErrors =>
        Status(apiErrors.statusCode) {
          JsObject(Seq(
            "status" -> JsString("error"),
            "statusCode" -> JsNumber(apiErrors.statusCode),
            "errors" -> Json.toJson(apiErrors.errors)
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
