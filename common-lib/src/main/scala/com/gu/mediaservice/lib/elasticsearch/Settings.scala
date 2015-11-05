package com.gu.mediaservice.lib.elasticsearch

import play.api.libs.json.Json

object IndexSettings {
  // TODO rename `english_s_stemmer` as its an analyzer not a stemmer - would require a reindex.
  val guAnalyzer = "english_s_stemmer"

  val englishSStemmer = Json.obj(
    "type" -> "custom",
    "tokenizer" -> "standard",
    "filter" -> Json.arr(
      "lowercase",
      "asciifolding",
      "english_possessive_stemmer",
      "gu_stopwords",
      "s_stemmer"
    )
  )

  val filter = Json.obj(
    "s_stemmer" -> Json.obj(
      "type" -> "stemmer",
      "language" -> "minimal_english"
    ),
    "gu_stopwords" -> Json.obj(
      "type" -> "stop",
      "stopwords" -> "_english_"
    ),
    "english_possessive_stemmer" -> Json.obj(
      "type" -> "stemmer",
      "language" -> "possessive_english"
    )
  )

  val analyzer = Json.obj(
    guAnalyzer -> englishSStemmer
  )

  val analysis = Json.obj(
    "filter" -> filter,
    "analyzer" -> analyzer
  )

  val imageSettings: String =
    Json.stringify(Json.obj(
      "analysis" -> analysis
    )
  )

}

object TestThese {
  val tests = """
    GET 'Charles James Mark Armitage cricket'
    [{
      "description": "Another enthralling cricket match where Mark Armitage's bat keeps failing",
      "title": "Charles James",
      "shouldMatch": true,
      "doesMatch": true
    },
    {
      "description": "Another enthralling cricket match where Mark Armitage's bat keeps failing",
      "title": "Charles James's",
      "match": false
    }]

    GET 'snoop'
    [{
      "description": "Snoop Dogg, 'If the ride is more fly, then you must buy.'",
      "title": "Charles James's",
      "shouldMatch": true,
      "doesMatch": true
    }]

    GET 'snooping'
    [{
      "description": "Snoop Dogg, 'If the ride is more fly, then you must buy.'",
      "title": "Charles James's",
      "shouldMatch": true,
      "doesMatch": true
    }]

    GET 'ai weiwei tate gallery'
    [{
      "description": "Ai Weiwei's work was scattered amongst the tate galleries.'",
      "title": "John Bombkin",
      "match": false,
      "shouldMatch": true,
      "doesMatch": false,
      "why": "gallery vs galleries"
    }]
    """
}
