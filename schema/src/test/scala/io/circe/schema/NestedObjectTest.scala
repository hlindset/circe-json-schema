package io.circe.schema

import cats.data.Validated.Invalid
import io.circe.{ Json, jawn }
import org.scalatest.flatspec.AnyFlatSpec

class NestedObjectTest extends AnyFlatSpec {
  val schemaJson = """
                   |{
                   |  "type": "object",
                   |  "required": ["a_nested_object"],
                   |  "properties": {
                   |    "a_nested_object": {
                   |      "required": ["nested_value_a", "nested_value_b"],
                   |      "type": "object",
                   |      "properties": {
                   |        "nested_value_a": {"type": "string", "minLength": 1},
                   |        "nested_value_b": {"type": "string", "minLength": 1}
                   |      }
                   |    }
                   |  }
                   |}
                   |""".stripMargin

  behavior.of("multiple nested schema violations")

  it should "not leak nulls from everit " in {
    val parsedSchemaJson = jawn.parse(schemaJson).getOrElse(throw new Exception("Unable to load schema"))
    val schema = Schema.load(parsedSchemaJson)
    val result = schema.validate(Json.obj(("a_nested_object" -> Json.obj())))

    result match {
      case Invalid(e) => e.toList.foreach(x => assert(x.keyword != null))
      case _          => assert(false, "schema validation result should have been invalid")
    }
  }
}
