/**
 * Конфигурация боковой панели навигации для документации Spring Boot Ollama Integration
 */

// @ts-nocheck

const sidebars = {
  docs: [
    {
      type: 'doc',
      id: 'intro',
      label: 'Введение'
    },
    {
      type: 'category',
      label: 'Начало работы',
      items: ['getting-started/installation'],
      collapsed: false
    },
    {
      type: 'category',
      label: 'API',
      items: ['api/overview', 'api/examples/insights'],
      collapsed: false
    },
    {
      type: 'category',
      label: 'Архитектура',
      items: ['architecture/overview'],
      collapsed: false
    },
    {
      type: 'category',
      label: 'Конфигурация',
      items: ['configuration/properties'],
      collapsed: false
    },
    {
      type: 'category',
      label: 'Мониторинг',
      items: ['monitoring/metrics'],
      collapsed: false
    },
    {
      type: 'category',
      label: 'Тестирование',
      items: ['testing/overview', 'testing/unit-tests', 'testing/integration-tests', 'testing/e2e-tests'],
      collapsed: false
    }
  ],
};

module.exports = sidebars;
