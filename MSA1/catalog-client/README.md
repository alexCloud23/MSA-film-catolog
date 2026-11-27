# ЛБ6. Клиент каталога фильмов

Сервис на порту `8081` читает фильмы из MSA1 через Spring Cloud OpenFeign. Запустите из корня проекта.

```powershell
docker compose up --build -d
```

После запуска MSA1 получите список и запросите карточку фильма по ID из списка.

```powershell
$films = Invoke-RestMethod http://localhost:8081/catalog/films
$films
Invoke-RestMethod "http://localhost:8081/catalog/films/$($films[0].id)"
docker compose logs catalog-client
```

Для каждого вызова Feign в журнале появятся HTTP-метод, путь, статус или тип ошибки соединения и длительность в миллисекундах. Клиент возвращает `404` для отсутствующего фильма и `503`, когда каталог недоступен.

Импортируйте [коллекцию ЛБ6](../postman_catalog_client_collection.json) в Postman. Запустите папку «Каталог работает» в Collection Runner. Postman пропускает сценарий `503`, пока MSA1 отвечает `200`. Для проверки отказа выполните `docker compose stop app`, отправьте запрос из папки «Каталог недоступен» и восстановите MSA1 командой `docker compose start app`.

Для запуска клиента без Docker при работающем MSA1 выполните из корня `.\mvnw.cmd -f catalog-client/pom.xml spring-boot:run`. Клиент по умолчанию обращается к `http://localhost:8080`. Переменная `CATALOG_BASE_URL` задаёт другой адрес.

Команда `.\mvnw.cmd -f catalog-client/pom.xml verify` проверяет клиент через локальный HTTP-сервер.
