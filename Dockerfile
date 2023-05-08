FROM postgres:13.3

ENV POSTGRES_DB=epigraphsBDD
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=1234

VOLUME /var/lib/postgresql/data

EXPOSE 5432

CMD ["postgres"]
