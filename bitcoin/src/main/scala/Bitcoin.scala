import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.api.{EnvironmentSettings, FieldExpression}
import org.apache.flink.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.functions.ScalarFunction

case class Bitcoin(hash: String, ts: String, amount: String)
object Bitcoin {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val bsSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
    val tEnv = StreamTableEnvironment.create(env, bsSettings)

    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
    env.enableCheckpointing(5000, CheckpointingMode.AT_LEAST_ONCE)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    env.getCheckpointConfig.setTolerableCheckpointFailureNumber(3)
    env.getConfig.enableObjectReuse()

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "test")

    val kafka_source_coin = new FlinkKafkaConsumer[String]("utx", new SimpleStringSchema(), properties)
    val kafka_source_taux = new FlinkKafkaConsumer[String]("taux", new SimpleStringSchema(), properties)

    val bitcoin = env.addSource(kafka_source_coin)
    val taux = env.addSource(kafka_source_taux)

    val tableBit = tEnv.fromDataStream(bitcoin, $"line")
    val tBit = tableBit.addOrReplaceColumns($"line".regexpReplace("{|}", "")).addColumns($"line")

    tBit.printSchema()
    env.execute("Bitcoin")
  }

  def removeCurly(str: String): String = {
    val stripCurly = "[{}]".r
    stripCurly.replaceAllIn(str, "")
  }
}

class Split extends ScalarFunction {
  def eval(s: String): Bitcoin = {
    val m = s.split(", ").flatMap(_.split(": ")).grouped(2).map(e => (e(0), e(1))).toMap
    new Bitcoin(m("transaction_hash"), m("transaction_timestamp"), m("transaction_total_amount"))
  }
}

object Split {val split = new Split()}
