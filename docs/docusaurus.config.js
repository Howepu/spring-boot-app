const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

// With JSDoc @type annotations, IDEs can provide config autocompletion
/** @type {import('@docusaurus/types').DocusaurusConfig} */
(module.exports = {
  title: 'Spring Boot Ollama Integration',
  tagline: 'Документация по интеграции Spring Boot с Ollama API',
  url: 'https://spring-boot-ollama.example.com',
  baseUrl: '/docs/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',
  organizationName: 'facebook', // Usually your GitHub org/user name.
  projectName: 'docusaurus', // Usually your repo name.

  presets: [
    [
      '@docusaurus/preset-classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          editUrl: 'https://github.com/facebook/docusaurus/edit/main/website/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl:
            'https://github.com/facebook/docusaurus/edit/main/website/blog/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'Spring Boot Ollama Integration',
        logo: {
          alt: 'Spring Boot Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'left',
            label: 'Документация',
          },
          {
            type: 'doc',
            docId: 'api/overview',
            position: 'left',
            label: 'API',
          },
          {
            type: 'doc',
            docId: 'architecture/overview',
            position: 'left',
            label: 'Архитектура',
          },
          {
            to: '/blog', 
            label: 'Последние изменения', 
            position: 'right'
          },
          {
            href: '/swagger-ui/index.html',
            label: 'Swagger UI',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Документация',
            items: [
              {
                label: 'Введение',
                to: '/docs/intro',
              },
              {
                label: 'API',
                to: '/docs/api/overview',
              },
              {
                label: 'Архитектура',
                to: '/docs/architecture/overview',
              },
            ],
          },
          {
            title: 'Ресурсы',
            items: [
              {
                label: 'Spring Boot',
                href: 'https://spring.io/projects/spring-boot',
              },
              {
                label: 'Ollama',
                href: 'https://ollama.com',
              },
              {
                label: 'Spring Boot Actuator',
                href: 'https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html',
              },
            ],
          },
          {
            title: 'Инструменты',
            items: [
              {
                label: 'Swagger UI',
                href: '/swagger-ui/index.html',
              },
              {
                label: 'Actuator',
                href: '/actuator',
              },
              {
                label: 'Prometheus метрики',
                href: '/actuator/prometheus',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Spring Boot Ollama Integration. Создано с помощью Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
});
