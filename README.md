# Notes

## Leverantör

Sundsvalls kommun

## Beskrivning
Notes är en tjänst som ansvarar för att lagra noteringar kopplade till en privatkund eller organisation.


## Tekniska detaljer

### Starta tjänsten

|Miljövariabel|Beskrivning|
|---|---|
|**Databasinställningar**||
|`spring.datasource.url`|JDBC-URL för anslutning till databas|
|`spring.datasource.username`|Användarnamn för anslutning till databas|
|`spring.datasource.password`|Lösenord för anslutning till databas|


### Paketera och starta tjänsten
Applikationen kan paketeras genom:

```
./mvnw package
```
Kommandot skapar filen `api-service-notes-<version>.jar` i katalogen `target`. Tjänsten kan nu köras genom kommandot `java -jar target/api-service-notes-<version>.jar`. Observera att en lokal databas måste finnas startad för att tjänsten ska fungera.

### Bygga och starta med Docker
Exekvera följande kommando för att bygga en Docker-image:

```
docker build -f src/main/docker/Dockerfile -t api.sundsvall.se/ms-notes:latest .
```

Exekvera följande kommando för att starta samma Docker-image i en container:

```
docker run -i --rm -p8080:8080 api.sundsvall.se/ms-notes

```

#### Kör applikationen lokalt

Exekvera följande kommando för att bygga och starta en container i sandbox mode:  

```
docker-compose -f src/main/docker/docker-compose-sandbox.yaml build && docker-compose -f src/main/docker/docker-compose-sandbox.yaml up
```


## 
Copyright (c) 2021 Sundsvalls kommun