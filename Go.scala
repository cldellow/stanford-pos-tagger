import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;

object MyScalaApp {
  def timeIt[A](msg: String)(f: => A): A = {
   val st = System.nanoTime
   val rv = f
   println(" " + msg + ": " + (System.nanoTime - st)/1000)
   rv
  }

  def main(args: Array[String]) {
    val oracles = List("betel.txt", "grey-cup.txt", "raf-northolt.txt").map { "texts/" + _ }

    val txts = oracles.map { scala.io.Source.fromFile(_).getLines().mkString(" ") }
    val tests = oracles.map { _ + ".correct" }.map {
      scala.io.Source.fromFile(_).getLines().mkString("")
    }
    val tagger = new MaxentTagger("english.tagger")
    if(true) {
      (1 to 1000).foreach { i =>
        timeIt("tag") {
          val rv = (1 to 100).par.map { j =>
            tagger.tagString(txts(j % txts.length)) -> tests(j % tests.length)
          }
          rv.foreach { case (actual, expected) =>
            if(actual != expected)
              println("error");
          }

//          println("    getTag = " + tagger.took/1000);
//          tagger.took = 0;
        }
      }
    } else {
      txts.foreach { txt =>
        println(tagger.tagString(txt));
      }
    }
  }
}
