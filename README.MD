https://favourable-rodie-java-service-b82e5859.koyeb.app/ - connect to this url

https://favourable-rodie-java-service-b82e5859.koyeb.app/swagger-ui/index.html# - swagger


local machine: http://localhost:8000/swagger-ui/index.html#


# Посібник з автентифікації API

Цей посібник пояснює, як пройти автентифікацію та використовувати API сервіс.

## Процес автентифікації

### 1. Автентифікація через Google OAuth2
1. Перейдіть за посиланням OAuth2:
   ```
   https://favourable-rodie-java-service-b82e5859.koyeb.app/oauth2/login/google
   ```
2. Оберіть свій обліковий запис Google, коли з'явиться вікно вибору
3. Після успішної автентифікації ви отримаєте access token та додаткову інформацію про автентифікацію

### 2. Автентифікація в Swagger UI
1. Відкрийте документацію Swagger UI:
   ```
   https://favourable-rodie-java-service-b82e5859.koyeb.app/swagger-ui/index.html#
   ```
2. Натисніть кнопку "Authorize" в інтерфейсі Swagger UI
3. У вікні авторизації введіть свій access token у форматі:
   ```
   your_access_token
   ```

## Використання API

### 1. Налаштування додаткової інформації
1. Перейдіть до розділу `additional-info-controller`
2. Введіть необхідну інформацію у доступних ендпоінтах
3. Після відправки ви отримаєте новий access token та пов'язану інформацію

### 2. Подальше використання
1. Для продовження використання API після виходу з системи:
    - Використовуйте новий access token
    - Натисніть "Authorize" в Swagger UI
    - Введіть новий токен у форматі BearerAuth
2. Тепер ви можете виконувати різні API запити за потребою
