# Тестовое задание для компании systeme.io

## Сборка и запуск в Docker

```bash
docker-compose up --build
```

## Просмотр результатов

```bash
docker-compose logs -f
```

## Остановка

```bash
docker-compose down
```

## После выполнения тестов

```bash
allure serve ./allure-results
```