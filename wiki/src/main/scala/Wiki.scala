
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._

object Wiki {
  def main(args: Array[String]): Unit = {
    println("start")
    val env = ExecutionEnvironment.getExecutionEnvironment
    val bTableEnv = BatchTableEnvironment.create(env)

    val path = "/home/nvorotnikov/IdeaProjects/flink-formation/bitcoin/wiki/src/resources/test.csv"
    val booksPath = "/home/nvorotnikov/IdeaProjects/flink-formation/bitcoin/wiki/src/resources/books.csv"
    val csvFile = env
      .readCsvFile[(String, String, String, String)](path, "\n", ",", null, true, null, false, null, null)

    val booksCsv = env
      .readCsvFile[(String, String, String, String)](booksPath, "\n", ";", null, true, null, false, null, null)

    val res = csvFile.join(booksCsv)
      .where(1)
      .equalTo(3)
      .apply((a, b) => (a._2, a._1, a._3, a._4, b._1, b._2, b._3))

    res.print()

    val tBooks = bTableEnv.fromDataSet(res, $"id", $"name", $"nb", $"price", $"Title", $"Author", $"Year")
    tBooks.select($"*")
    val res1 = tBooks.select($"*").execute()
    res1.print
  }
}
