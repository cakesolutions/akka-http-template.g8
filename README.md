# akka-http-template.g8

A Giter8 template for creating a new project using the latest Akka-HTTP libraries and best practises suggested by the
Cake Team.

## Using the Template

To use, ensure that you have SBT 0.13.13 or higher installed.

Then:

```
sbt new cakesolutions/akka-http-template.g8
```

You will be asked to fill out some properties required for correctly building your project, once complete `giter8` will
create an SBT project layout with our opinionated layout and configuration.  You can now move to your new project it
will have all of the lovely features outlined above.  This should free you up to solve the really tough problems in the
project.

Following the creation of a project, you will need to configure the following additional items (they are all identified
by ```FIXME```s):
* ```sbt.resolvers``` - this file should ideally be pinned to use a project specific Nexus or Artifactory for caching
  and managing project library dependencies. **All** resolver settings should be placed in this file.
* ```build.sbt``` - this file needs the following autoplugin setting keys to be configured:
  * ```snapshotRepositoryResolver``` - this URL should point to the Nexus or Artifactory snapshot repository
  * ```repositoryResolver``` - this URL should point to the Nexus or Artifactory publishing repository
  * ```issueManagementUrl``` - this URL should point to the Jira project management API
  * ```issueManagementProject``` - this string should name the Jira project against which the project code is managed.
* ```project/ProjectPlugin.scala``` - this file needs the following autoplugin setting keys to be configured:
  * ```startYear``` - this integer list should be set to the yers that copyrights will be enforced in and from
  * ```licenses``` - this should be altered to the particular commercial or open source license that a project will use
  * ```headerLicense``` - this should be altered to the particular commercial or open source license comment header that
    will be added to each file.

## Contributing to the Template

This template will be an evolving opinion, PRs are welcome.

To test the output of the template you can run it locally with the following command:

```
sbt new file://./akka-http-template.g8
```

# Project Template Features

TODO:

## Key Build Files

### `project/project/Dependencies.scala`

TODO: mention limitation in that it is not easy to use CakePlatformPluginKeys here

### `project/ProjectPlugin.scala`

TODO:

### `project/ProjectDockerBuildPlugin.scala`

TODO:

## Application Bootstrapping

TODO: sys.exit control; exit codes; start-up logging; Monix tasks

### Retrying on Bootstrapping Errors

TODO: backoff logic; limited vs unlimited retries

## Validated Configuration

TODO: need to validate; need to generate Typesafe config **from** validated config (don't use the underlying raw object!)

## Logging

TODO: start-up logging; MDC structured logging; Zipkin logging

# AWS Jenkins Debugging

Should you be unfortunate enough to find yourself in a situation where you need to physically debug a build within a
Jenkins environment, then we can achieve this as follows:
* login and pull the relevant Jenkins Docker slave image into your local development Docker registry
  * if using AWS ECR to host Jenkins Docker slave images (e.g. using profile `cakesolutions` and region `XXX` to pull
    version `1.2.3` of the image `ZZZ`), then enter a variant of the following:
    ```bash
    docker login $(aws --profile cakesolutions ecr --region XXX get-login)
    docker pull ZZZ:1.2.3
    ```
* run the local Jenkins Docker slave image with a `bash` entry point
  ```bash
  docker run -v /var/run/docker.sock:/var/run/docker.sock -it ZZZ bash
  # You should now be at the running containers command line prompt
  ```
* ensure any specialised container packages are installed
  * for example, it might be necessary to install `ncurses` within a base Alpine image, in which case you would enter a
    variant of the following:
    ```bash
    apk add --update ncurses
    ```
* ensure any required environment variables are set
  * for example:
    ```bash
    export CI=docker-local
    export DOCKER_COMPOSE_PROJECT_NAME=local
    ```
* clone and manually build your project code - for example, to compile feature branch `WWW` for project `YYY`:
  ```base
  git clone https://github.com/cakesolutions/YYY.git
  cd YYY
  git checkout feature/WWW
  sbt clean compile
  ```

# Releasing of the `akka-http-template.g8` Template

In order to release version `X.Y.Z` of the `akka-http-template.g8` template, perform the following actions:
```
git tag -s vX.Y.Z
git push --tags
```

Releasing of templates is simply a matter of adding tags to the github repository.
