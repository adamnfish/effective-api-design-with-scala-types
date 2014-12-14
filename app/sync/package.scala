import models.ApiErrors

package object sync {
  type ApiResponse[T] = Either[ApiErrors, T]
}
