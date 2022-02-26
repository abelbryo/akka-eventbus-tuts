package com.terefe

import play.api.libs.json._

class HelloSpec extends munit.FunSuite {

  case class TestPerson(name: String, age: Int)
  object TestPerson:
    given tpJsonFmt: Format[TestPerson] = Json.format[TestPerson]

  test("Json.format in scala 3 should work same as in scala 2") {

    val p = TestPerson("Jill", 10)
    val json = Json.toJson(p)

    assertEquals(json.as[TestPerson], p)
  }
}
