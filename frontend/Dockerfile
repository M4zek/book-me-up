FROM node:22-alpine as build

WORKDIR /usr/local/app

COPY ./ /usr/local/app/

RUN npm install --force
RUN npm run build -- --configuration=development 

FROM nginx:latest

COPY --from=build /usr/local/app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
