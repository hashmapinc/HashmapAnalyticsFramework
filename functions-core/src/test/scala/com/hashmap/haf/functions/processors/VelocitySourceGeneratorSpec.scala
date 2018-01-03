package com.hashmap.haf.functions.processors

import com.hashmap.haf.models.IgniteFunctionType
import org.specs2.mutable.Specification

class VelocitySourceGeneratorSpec extends Specification{

	"Velocity source generator" should {
		"generate source from template" in {
			val result = """package com.hashmap.haf.functions.extension
				             |
				             |import scala.xml.NodeSeq
				             |import com.hashmap.haf.functions.services.ServiceFunction
				             |import org.apache.ignite.resources.ServiceResource
				             |
				             |case class TestTask(xml: NodeSeq, configs: Map[String, String]){
				             |
				             |  @ServiceResource(serviceName = "testService", proxyInterface = classOf[ServiceFunction])
				             |  protected val mapSvc = _
				             |
				             |  def execute(): Unit = {
				             |    mapSvc.run(inputCache, outputCache, confs)
				             |  }
				             |}""".stripMargin

			val v = new VelocitySourceGenerator
			v.template = "test.vm"
			val source = v.generateSource(new IgniteFunctionType("testService", Array(), "TestTask", "com.hashmap.haf.functions.extension"))
			source shouldEqual Right(result)
		}

		"error out when non existing template given" in {
			val v = new VelocitySourceGenerator
			v.template = "doesNotExist.vm"
			val source = v.generateSource(new IgniteFunctionType("testService", Array(), "TestTask", "com.hashmap.haf.functions.extension"))
			source.isLeft shouldEqual true
			source.left.toOption.get._1 shouldEqual "Error while loading template"
		}
	}

}
