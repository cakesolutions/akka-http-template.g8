package $package$.core.application.workflow

/**
  * Exception message indicating that a task's retry logic has failed.
  *
  * @param message @see java.lang.Exception
  */
final class RetryExceededException(message: String)
    extends Exception(message) {

  /** @see java.lang.Exception */
  override def toString: String = {
    s"RetryExceededException(\$message)"
  }
}
