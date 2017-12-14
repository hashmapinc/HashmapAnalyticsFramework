import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.api.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.models.IgniteFunctionType

/**
	* Created by jay on 13/12/17.
	*/
object AnnotationsProcessorSpec extends App{

	private val value = ProcessorFactory[IgniteFunction, IgniteFunctionType]
	println(value)

}
