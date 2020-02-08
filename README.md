# Nim

[![CircleCI](https://circleci.com/gh/SoulBeaver/nim.svg?style=svg)](https://circleci.com/gh/SoulBeaver/nim) [![codecov](https://codecov.io/gh/SoulBeaver/nim/branch/master/graph/badge.svg?token=tQkCabXP3F)](https://codecov.io/gh/SoulBeaver/nim)

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

## Tech Stack

- Kotlin
- Http4k
- MongoDB

## Developer Thoughts

Hier kommen meine Gedanken zum Design und technischen Umsetzung des Projekts.

### Wieso HTTP4k?

In meiner jetzigen Stelle arbeite ich bereits viel mit Spring Boot und wollte das nicht nochmal anwenden für
eine Test-Aufgabe. Tatsächlich habe ich http4k für einigen Monaten ausprobiert und somit natürlich auch alles schon wieder vergessen :)

Was mich daran interessierte waren die neuen vorangehensweisen, wie Server as a Function und Lenses. Wie das Projekt letztendlich ausgefallen ist, seht ihr ja selbst.
Nicht alles hat zu 100% geklappt und ich musste mich erst noch in die Library hineinfuchsen. Es ist so also ein nettes Beispiel, wie ich mit neuen Technologien
umgehe und mich darin produktiv gestalte.

Außerdem ist http4k ziemlich schnell. Klar, Performance muss nicht unbedingt das höchste Kriterium sein, aber
Serverkosten zu sparen ist nicht zu unterschätzen, vorallem wenn es nicht signifikant mehr Aufwand und Expertise verbinden ist. Und Spring Boot ist schon *echt* langsam. 

### Wieso MongoDB statt *SQL?

MongoDB hat sich hier wegen der Einfachheit angeboten. Jede Operation ist atomic und transactions braucht man hier nicht wirklich. Desweiteren konnte
ich eine embedded MongoDB für Integrationstests benutzen und vermeide somit Schema und Queries.

Ich glaube die Frage ist dann eher, ob ich MongoDB benutzt hätte, wenn dies ein Projekt ohne feste Requirements wäre. Wenn ich wüsste, dass sich die Anforderungen
hier weiter entfalten und ich das Projekt auf unbestimmte Zeit maintainen muss... dann wahrscheinlich nicht.

### Ist dieses Projekt noch KISS?

Klar. 

Glaube ich.

Also, ich hab ein bisschen mit der Library rumgespielt. Ich hätte die Endpoints nicht alle in verschiedene
Dateien schmeißen müssen, die Validation ist auch mehr Code als es mir lieb ist und Undo war eher ein, "Oh, das geht ja
einfach mit einzufügen." Es steht zwar nicht in den Requirements, aber als User fand ich es nervig nach einem schlechten Play
ein neues Spiel anzufangen und das alte Spiel zu löschen. 

### Was für Edge Cases wurden berücksichtigt?

- JSON Validation. Zumindest das, was http4k als Validation versteht. Man
darf nicht zuviele matchsticks nehmen, das Spiel kann nicht unter 0 gehen, so etwas halt.
- Race conditions. Selbst im `/:id/take` habe ich darauf geachtet, dass die Operation atomisch bleibt, 
damit MongoDB nicht versucht den Spielerzug zu inserten, bevor der PC dran war und plötzlich noch ein neuer Request ankommt.
MongoDB lockt das Dokument und andere versuche, das Spiel zu beeinflussen werden zwar nicht abgelehnt, aber sie müssen warten,
bis der Write fertig ist. Für diese Aufgabe finde ich die Lösung okay.
- Exceptional Behavior. Ich bin kein großer Freund von vielen Exceptions. Das meiste, was es an Input in diesem Projekt gibt
wird leise behandelt oder als 400 zurückgegeben. Es gibt keinen Grund, erwartetes Fehlverhalten als Exceptions zu behandeln.
- Logging. Metriken fehlen, ja, aber das wichtigste an Microservices sind tracing, logging und metrics. Tracing und Metrics war in diesem Projekt nicht die Arbeit
wert, aber Logging auf jeden Fall. Ist auch einfach mit einzufügen, sonst würde ich http4k gar nicht erst empfehlen.
- OpenAPI. Eine Swagger Doku ist ziemlich cool.
- Healthchecks. Gibt nur einen, aber der reicht erstmal. 

Es ist nicht wirklich ein Edge-Case, aber Dokumentation Englisch-Deutsch ist immer so eine Sache. Ich bevorzuge es, wenn
der Code und Code Comments in Englisch bleiben, das Readme und andere Dokumentation können aber in Deutsch bleiben.

### Was steht noch aus?

Andere Microservice-Patterns wie:

- Circuit Breaking
- Bulkheads
- Retrys

End-to-End tests wären noch sinnvoll und meine Code Coverage will irgendwie nicht so, wie ich will.

In diesem Fall waren die einfach nicht notwendig. Wir kommunizieren mit keinen anderen Services
und ich gehe stark davon aus, dass ihr mir keine Load Tests reinjagt, um meinen Service zu überfordern :D

### Was war für mich persönlich wichtig an diesem Projekt?

Dokumentation! In Form dieser README, im Code, wenn es sinnvoll ist, durch gute Test-Abdeckung und das nicht oft-genug benutzte `requests.http`.
Ich glaube, die meisten Entwickler sind in der Lage, ihren Kopf in eine Codebase zu stecken und produktiv zu sein, aber 
mit jedem fehlenden Stück Dokumentation dauert es länger, wirkt es frustrierender und verlangsamt die Arbeit. Mehr als alles
andere ist das lästige Thema Dokumentation- in ihren verschiedensten Formen- eines der wichtigsten Bestandteile eines Projekts.

Klar, Dokumentation ist auch schnell veraltet. In einem Projekt wie diesem veraltet die README am schnellsten, dann wohl die `requests.http`,
dann Kommentare im Code und zuletzt die Tests. Das sollte uns nicht davon abhalten, durch gute und rigöröse (und gemeinsam beschlossene) Best Practices
darauf hinzuweisen, die Dokumentation wenn möglich immer aktuell zu halten.