# CloudHoarder

Пишем облачное хранилище

Используем клиент-серверную архитектуру. Для сети используем сокеты.
Пусть клиент использует JavaFX. Нужна аутентификация.

Основная задача: передавать файлы с сервера на клиент (скачивать), и с клиента
на сервер (заливать файлы в облако)

Файлы на сервере хранятся в папке (репозитории), для каждого клиента создается
отдельная подпапка с его логином. На первом этапе подкаталоги не нужны.

Необходим базовый функционал по работе с файлами: переименование, удаление,
получение списка файлов как в локальном хранилище, так и в облаке

Как будем передавать файлы? Передаем файлы: в виде "чистого" байт-массива
(свой протокол передачи), или используя сериализацию бросаемся объектами

Надо ли передавать сообщения/команды? Надо. А как? Нужно ли открывать
отдельное сетевое соединение, или запускать еще Thread'ы?

Нужна ли нам БД для хранилища?

Как организовать передачу больших файлов?

* Очень дополнительные возможности:
- Подкаталоги
- Докачка файлов
- Синхронизация локальное хранилище/облако
- Шифрование