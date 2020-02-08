# Nim

[![codecov](https://codecov.io/gh/SoulBeaver/nim/branch/master/graph/badge.svg?token=tQkCabXP3F)](https://codecov.io/gh/SoulBeaver/nim)

## Das Spiel

Das Nim-Spiel ist ein Spiel für 2 Personen. Gegeben ist ein Haufen von 13 Streichhölzern, von dem beide Spieler abwechselnd entweder 1, 2 oder 3 Streichhölzer ziehen müssen. 
Wer das letzte Streichholz erhält hat verloren.

Das Nim-Spiel hat verschiedene Varianten. Bei der beschriebenen Variante geht es um das Misère-Spiel in einer Reihe bzw. einem Haufen.

Die Anzahl der Streichhölzer, welche der Computer zieht, kann zufällig gewählt werden. Dabei darf diese nicht die aktuell
verfügbaren Streichhölzer überschreiten. Das heißt, wenn nur noch 2 Streichhölzer auf dem Haufen liegen, darf der Computer auch maximal 2 Streichhölzer ziehen.

## API

### Überblick

Alle Endpunkte können auch in der Datei `requests.http` mit einem lokal laufendem Programm ausgeführt werden.

|METHOD|ENDPOINT|DESCRIPTION|
|---|---|---|
|GET|/nim|Get all Nim games
|GET|/nim/:id|Get specific Nim game for given id
|GET|/nim/completed|Get all completed Nim games
|GET|/nim/active|Get all active Nim games
|POST|/nim|Create new Nim game
|POST|/nim/:id/take|Take matchsticks from active Nim game
|POST|/nim/:id/undo|Undo the current turn
|PUT|/nim/:id|Set the Nim game to a different state
|DELETE|/nim/:id|Delete the Nim game for the given id if it exists

### Endpunkte

#### GET /nim

#### GET /nim/:id

#### GET /nim/completed

#### GET /nim/active

#### POST /nim

#### PUT /nim/:id/take

#### PUT /nim/:id/undo

#### PUT /nim/:id

#### DELETE /nim/:id

### Beispiel des Datenmodells eines aktuell laufenden Spiels:

Beispielrequest:

`GET /nim/1`

Response:

```json
{
  "nim_game_id": 1,

  "turn": 1,
  "match_sticks_remaining": 13,

  "game_history": [],

  "actions": [
    {
      "type": "TAKE_ONE",
      "rel": "/nim/1/take",
      "method": "PUT",
      "payload": {
        "turn": 3,
        "match_sticks_taken": 1
      }
    },
    {
      "type": "TAKE_TWO",
      "rel": "/nim/1/take",
      "method": "PUT",
      "payload": {
        "turn": 3,
        "match_sticks_taken": 2
      }
    },
    {
      "type": "TAKE_THREE",
      "rel": "/nim/1/take",
      "method": "PUT",
      "payload": {
        "turn": 3,
        "match_sticks_taken": 3
      }
    }
  ]
}
```

### Beispiel eines erfolgreich beendeten Spiels

```json
{
  "id": 1,

  "turn": 1,
  "match_sticks_remaining": 13,

  "game_history": [],

  "actions": [
    {
      "type": "TAKE_ONE",
      "rel": "/nim/1/take",
      "method": "PUT",
      "payload": {
        "turn": 1,
        "match_sticks_taken": 1
      }
    },
    {
      "type": "TAKE_TWO",
      "rel": "/nim/1/take",
      "method": "PUT",
      "payload": {
        "turn": 1,
        "match_sticks_taken": 2
      }
    },
    {
      "type": "TAKE_THREE",
      "rel": "/nim/1/take",
      "method": "PUT",
      "payload": {
        "turn": 1,
        "match_sticks_taken": 3
      }
    }
  ]
}
```

## Developer Thoughts

Hier kommen meine Gedanken zum Design und technischen Umsetzung des Projekts.

### Wieso HTTP4k?

### Wieso MongoDB statt *SQL?

### Wieso PUT für /nim/:id/take und /nim/:id/undo und nicht POST?

### Ist dieses Projekt noch KISS?

### Was für Edge Cases wurden berücksichtigt?

### Was steht noch aus?

### Was war für mich persönlich wichtig an diesem Projekt?

Dokumentation! In Form dieser README, im Code, wenn es sinnvoll ist, durch gute Test-Abdeckung und das nicht oft-genug benutzte `requests.http`.
Ich glaube, die meisten Entwickler sind in der Lage, ihren Kopf in eine Codebase zu stecken und produktiv zu sein, aber 
mit jedem fehlenden Stück Dokumentation dauert es länger, wirkt es frustrierender und verlangsamt die Arbeit. Mehr als alles
andere ist das lästige Thema Dokumentation- in ihren verschiedensten Formen- eines der wichtigsten Bestandteile eines Projekts.

Klar, Dokumentation ist auch schnell veraltet. In einem Projekt wie diesem veraltet die README am schnellsten, dann wohl die `requests.http`,
dann Kommentare im Code und zuletzt die Tests. Das sollte uns nicht davon abhalten, durch gute und rigöröse (und gemeinsam beschlossene) Best Practices
darauf hinzuweisen, die Dokumentation wenn möglich immer aktuell zu halten.