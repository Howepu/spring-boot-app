import React from 'react';
import clsx from 'clsx';
import styles from './HomepageFeatures.module.css';

const FeatureList = [
  {
    title: 'Интеграция с Ollama API',
    Svg: require('../../static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        Интеграция с локально запущенным Ollama API для работы с нейросетевыми моделями 
        и получения аналитических данных на основе нейросети.
      </>
    ),
  },
  {
    title: 'Современная архитектура',
    Svg: require('../../static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        Приложение построено на Spring Boot с использованием современных практик: 
        многоуровневая архитектура, REST API, мониторинг через Spring Boot Actuator.
      </>
    ),
  },
  {
    title: 'Удобный интерфейс на Bootstrap',
    Svg: require('../../static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        Веб-интерфейс реализован на Bootstrap, обеспечивая удобство использования, адаптивность
        и быструю загрузку на всех устройствах.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} alt={title} />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
