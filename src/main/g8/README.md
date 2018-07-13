# $name$

TODO: CO-176: Add detailed documentation to the template project

## Compilation

Compile the project by running:

```bash
> sbt compile
```

## Testing

Run the unit tests for the project by running:

```bash
> sbt test
```

Run the integration tests for the project by running:

```bash
> sbt dockerComposeUp # Start the docker environment
> sbt it:test # Run the integration tests
> sbt dockerComposeDown # Clean up the docker environment
```

<!--
  Created from version $akka_template_version$ of https://github.com/cakesolutions/akka-http-template.g8
-->