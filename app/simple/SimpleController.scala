package simple

import models.{Access, User, UserResponse}
import play.api.libs.json.{Json, Writes}
import play.api.mvc._


/**
 * Examples of how an API might traditionally be written.
 */
object SimpleController extends Controller {
  /**
   * Simplified update user example
   */
  def updateUser(userId: String) = Action { request =>
    val access: Access = Authentication.getAccess(request, userId)
    if (access.isOk) {
      val submission: User = UserData.fromSubmission(request)
      val updatedUser: User = UserRepository.update(userId, submission)
      val userResponse = UserResponse(updatedUser)
      Ok(Json.toJson(userResponse))
    } else {
      Forbidden("Access denied")
    }
  }

  /**
   * More realistic update user example
   */
  def updateUser2(userId: String) = Action { request =>
    val access: Access = Authentication.getAccess(request, userId)
    if (access.isOk) {
      try {
        val submission: User = UserData.fromSubmission(request)
        val updatedUser: User = UserRepository.update(userId, submission)
        val userResponse = UserResponse(updatedUser)
        Ok(Json.toJson(userResponse))
      } catch {
        case e: UserData.Invalid => BadRequest("Invalid user data")
        case e: UserData.InvalidEmailAddress => BadRequest("Invalid email address")
        case e: DatabaseError => InternalServerError("Could not connect to database")
      }
    } else {
      Forbidden("Access denied")
    }
  }

  /**
   * Even more realistic update user example
   */
  def updateUser3(userId: String) = Action { request =>
    val access = Authentication.getAccess(request, userId)
    if (access.isOk) {
      try {
        val (errors, submissionOption) = UserData.attemptFromSubmission(request)
        if (submissionOption.isDefined) {
          val updatedUser = UserRepository.update(userId, submissionOption.get)
          val userResponse = UserResponse(updatedUser)
          Ok(Json.toJson(userResponse))
        } else {
          val submissionErrors = ValidationErrorsResponse(errors)
          BadRequest(Json.toJson(submissionErrors))
        }
      } catch {
        case e: UserData.Invalid => BadRequest("Invalid user data")
        case e: DatabaseError => InternalServerError("Could not connect to database")
      }
    } else {
      Forbidden("Access denied")
    }
  }


  // some required stubs
  implicit val urWrites: Writes[UserResponse] = ???
  object Authentication {
    def getAccess(request: RequestHeader, userId: String): Access = {
      // check for auth cookie
      // check for access token
      // check credentials in database
      throw new DatabaseError()
      // check credentials match userId
      Access.OK
    }
  }
  object UserData {
    def fromSubmission(r: RequestHeader): User = ???
    def attemptFromSubmission(request: RequestHeader): (List[Throwable], Option[User]) = ???
    class Invalid extends Throwable
    class InvalidEmailAddress extends Throwable
  }
  object UserRepository {
    def update(userId: String, u: User): User = ???
  }
  class DatabaseError extends Throwable
  case class ValidationErrorsResponse(errs: List[Throwable])
  implicit val verWrites: Writes[ValidationErrorsResponse] = ???
}
