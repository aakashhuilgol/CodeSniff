# Use Node.js as a base image to run ESLint
FROM node:18

# Set working directory inside the container
WORKDIR /app

# Install ESLint globally
# Install ESLint and plugins globally
RUN npm install -g eslint@8 eslint-plugin-import eslint-plugin-sonarjs
# Set NODE_PATH to use global npm modules
ENV NODE_PATH=/usr/local/lib/node_modules



# Run ESLint analysis on the specified directory and output results to eslint_report.txt
CMD ["eslint", "/app", "-f", "json", "-o", "/app/eslint_report.txt", "--ignore-pattern", "eslint.config.js", "--ignore-pattern", "depth-of-inheritance-tree.js"]
