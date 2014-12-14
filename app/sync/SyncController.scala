package sync

import models.{Access, ApiError, ApiErrors, User}
import play.api.mvc._


/**
 * Examples of an API that uses Either as a consistent response type.
 */
class SyncController extends Controller {

  def updateUser(userId: String) = Action { request =>
    ApiResponse {
      for {
        access <- Authentication.getAccess(request, userId).right
        submission <- UserData.fromSubmission(request).right
        updatedUser <- UserRepository.update(userId, submission).right
      } yield updatedUser
    }
  }

  // some required stubs
  object DatabaseDriver {
    def find(): Any = ???
  }
  object Authentication {
    def getAccess(request: RequestHeader, userId: String): ApiResponse[Access] = {
      // check for auth cookie
      // check for access token
      // ensure valid credentials
      Left(ApiErrors(List(ApiError("Database connection failure", "We were unable to check your credentials, please try again shortly", 500))))
      // check credentials match userId
      Right(Access.OK)
    }
  }
  object UserData {
    def fromSubmission(request: RequestHeader): ApiResponse[User] = ???
  }
  object UserRepository {
    def update(s: String, user: User): ApiResponse[User] = ???
  }
}
