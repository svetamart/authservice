FROM postgres:13.2-alpine
ENV POSTGRES_DB auth-service-db
ENV POSTGRES_USER user
ENV POSTGRES_PASSWORD secret