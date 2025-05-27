# To start a server locally

1. git clone https://github.com/engly817chat/engly-server.git
2. In a console write - ***.\generate_keys.sh***;
3. Use env variables in IntelliJ IDEA with this:
   `BACKEND_URL=localhost;
    BASE_URL=http://localhost:8000;
    DB_HOST=localhost:5432;
    DB_NAME=postgres;
    DB_PASSWORD=postgres;
    DB_USER=postgres;
    DEV_EMAIL=dev@email;
    ENGLY_EMAIL=some@gmail.com;
    ENGLY_EMAIL_PASSWORD=password;
    FRONTEND_URL=http://localhost:3000;
    GOOGLE_CLIENT_ID=someid;
    GOOGLE_CLIENT_SECRET=secret;
    SYS_EMAIL=sys@email`;
4. Run