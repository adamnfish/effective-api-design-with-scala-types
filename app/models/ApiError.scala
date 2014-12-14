package models

import play.api.libs.json._


case class ApiError(message: String, friendlyMessage: String,
                    statusCode: Int, context: Option[String] = None)

object ApiError {
  implicit val format = Json.format[ApiError]
  def unexpected(message: String) = ApiError(message, "Unexpected error", 500)
}

case class ApiErrors(errors: List[ApiError]) {
  def statusCode = errors.map(_.statusCode).max
}

object ApiErrors {
  implicit val format = Json.format[ApiErrors]
}
