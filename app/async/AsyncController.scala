package async

import models.{User, Access}
import play.api.mvc._


/**
 * Examples of an asyncronous API that uses our custom-made
 * ApiResponse type.
 */
class AsyncController extends Controller {

  def updateUser(userId: String) = Action.async { request =>
    ApiResponse {
      for {
        access <- Authentication.getAccess(request, userId)
        submission <- UserData.fromSubmission(request)
        updatedUser <- UserRepository.update(userId, submission)
      } yield updatedUser
    }
  }

  // some required stubs
  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global
  object Authentication {
    def getAccess(request: Request[AnyContent], s: String): ApiResponse[Access] = ???
  }
  object UserData {
    def fromSubmission(request: Request[AnyContent]): ApiResponse[User] = ???
  }
  object UserRepository {
    def update(s: String, user: User): ApiResponse[User] = ???
  }
}
