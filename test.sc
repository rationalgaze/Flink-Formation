val str = "{\"transaction_hash\": \"fc7de5c4a00a8fdd4cbea9df0f9a711963df01aa8c5841fdcb4d913fa2998e28\", \"transaction_timestamp\": 1601912292, \"transaction_total_amount\": 0.04679528}"
val stripCurly = "[{}]".r

val replaced = stripCurly.replaceAllIn(str, "")
val m = replaced.split(", ").flatMap(_.split(": ")).grouped(2).map(e => (e(0), e(1))).toMap
println(m.keys)
println(m.values)
println(m.contains("transaction_hash"))
val colors = Map("red" -> "#FF0000", "azure" -> "#F0FFFF", "peru" -> "#CD853F")
colors("red")

val weather =
  <rss>
    <channel>
      <title>Yahoo! Weather - Boulder, CO</title>
      <item>
        <title>Conditions for Boulder, CO at 2:54 pm MST</title>
        <forecast day="Thu" date="10 Nov 2011" low="37" high="58" text="Partly Cloudy"
                  code="29" />
      </item>
    </channel>
  </rss>

val forecast = weather \ "channel" \ "item" \ "forecast"
val day = (forecast \ "@day").text
val date = weather \ "channel" \ "item" \ "forecast" \ "@date"