FROM nginx:1.13.9-alpine

## Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

COPY /dist /usr/share/nginx/html
