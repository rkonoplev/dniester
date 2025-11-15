FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --registry https://registry.npmjs.org/ --verbose

COPY . .

RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]