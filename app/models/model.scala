package models

import play.api.libs.json._


case class User(userId: String)
object User {
  implicit val jsWrite = Json.writes[User]
}
case class UserResponse(u: User)
case class Access(isOk: Boolean)
object Access {
  val OK = Access(true)
  val failure = Access(false)
}