FROM grafana/k6:0.49.0

WORKDIR /performance
COPY . /performance

ENTRYPOINT ["k6"]
CMD ["run", "tests/smoke.test.js"]
