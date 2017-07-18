package $organisation_domain$.$organisation$.$name$.core.application.workflow

/**
  * TODO:
  */
final class RetryExceededException(message: String)
    extends Exception(message) {

  /** @see java.lang.Exception */
  override def toString: String = {
    s"RetryExceededException(\$message)"
  }
}
