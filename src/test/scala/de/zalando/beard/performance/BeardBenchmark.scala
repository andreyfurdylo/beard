package de.zalando.beard.performance

import de.zalando.beard.parser.BeardTemplateParser
import de.zalando.beard.renderer.{TemplateName, BeardTemplateRenderer, DefaultTemplateCompiler}
import org.scalameter.api._

import scala.io.Source

/**
 * @author dpersa
 */
object BeardBenchmark extends Bench.LocalTime {
  val compiler = DefaultTemplateCompiler
  val renderer = new BeardTemplateRenderer(compiler)

  val template = BeardTemplateParser {
    Source.fromInputStream(getClass.getResourceAsStream("/templates/layout-with-partial.beard")).mkString
  }

  compiler.compile(TemplateName("/templates/layout-with-partial.beard"))

  val context: Map[String, Map[String, Object]] = Map("example" -> Map("title" -> "Title", "presentations" ->
    Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
        Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2"))))

  val sizes = Gen.range("size")(1, 100000, 5000)
  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Beard" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) => {
          r.foreach { _ =>
            renderer.render(template, context)
          }
        }
      }
    }
  }
}