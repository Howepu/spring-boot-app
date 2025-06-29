// Компонент формы для запроса инсайтов с использованием хуков состояния
function InsightForm() {
  // Состояние для хранения введенной темы
  const [topic, setTopic] = React.useState('');
  
  // Состояние для хранения статуса загрузки
  const [isLoading, setIsLoading] = React.useState(false);
  
  // Состояние для хранения сообщения об ошибке
  const [error, setError] = React.useState(null);
  
  // Состояние для хранения результатов от API
  const [results, setResults] = React.useState(null);

  // Функция для обработки изменений в поле ввода
  const handleTopicChange = (e) => {
    setTopic(e.target.value);
    // Сбрасываем ошибку при изменении ввода
    if (error) setError(null);
  };

  // Функция для генерации инсайтов при отправке формы
  const generateInsights = async (e) => {
    e.preventDefault();
    
    // Проверка наличия темы
    if (!topic.trim()) {
      setError('Пожалуйста, введите тему для анализа');
      return;
    }
    
    try {
      // Устанавливаем статус загрузки
      setIsLoading(true);
      setError(null);
      setResults(null);
      
      // Выполняем POST-запрос к API
      const response = await fetch('/api/insights', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ topic: topic.trim() }),
      });
      
      // Парсим ответ как JSON
      const data = await response.json();
      
      // Проверяем успешность запроса
      if (!response.ok) {
        throw new Error(data.message || 'Произошла ошибка при получении данных');
      }
      
      // Если запрос успешен, обновляем состояние с результатами
      setResults(data);
      
    } catch (err) {
      // В случае ошибки, обновляем состояние с сообщением об ошибке
      setError(err.message || 'Произошла неизвестная ошибка');
    } finally {
      // В любом случае снимаем статус загрузки
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto">
      <div className="bg-white rounded-lg shadow-md p-6">
        <form onSubmit={generateInsights}>
          <div className="mb-4">
            <label htmlFor="topic" className="block text-sm font-medium text-gray-700 mb-1">
              Тема для анализа
            </label>
            <input
              type="text"
              id="topic"
              name="topic"
              value={topic}
              onChange={handleTopicChange}
              placeholder="Введите интересующую вас тему..."
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-primary focus:border-primary"
              disabled={isLoading}
            />
          </div>
          
          <div className="flex justify-center">
            <button 
              type="submit" 
              className="btn-primary flex items-center" 
              disabled={isLoading}
            >
              {isLoading && (
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              )}
              {isLoading ? 'Генерация...' : 'Сгенерировать инсайты'}
            </button>
          </div>
        </form>
        
        {/* Отображение ошибки */}
        {error && (
          <div className="mt-6 p-4 bg-red-50 border border-red-200 rounded-md">
            <p className="text-red-700">{error}</p>
          </div>
        )}
        
        {/* Отображение результатов */}
        {results && (
          <div className="mt-8">
            <h2 className="text-xl font-bold mb-4">Результаты анализа</h2>
            
            {/* Обзор */}
            {results.overview && (
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Обзор</h3>
                <p className="text-gray-600">{results.overview}</p>
              </div>
            )}
            
            {/* Ключевые понятия */}
            {results.keyConcepts && results.keyConcepts.length > 0 && (
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Ключевые понятия</h3>
                <ul className="list-disc pl-5 space-y-1">
                  {results.keyConcepts.map((concept, index) => (
                    <li key={index} className="text-gray-600">{concept}</li>
                  ))}
                </ul>
              </div>
            )}
            
            {/* Связанные ресурсы */}
            {results.relatedResources && results.relatedResources.length > 0 && (
              <div>
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Полезные ресурсы</h3>
                <ul className="list-disc pl-5 space-y-2">
                  {results.relatedResources.map((resource, index) => (
                    <li key={index}>
                      <a 
                        href={resource.url} 
                        target="_blank" 
                        rel="noopener noreferrer"
                        className="text-primary hover:underline"
                      >
                        {resource.title}
                      </a>
                      {resource.description && (
                        <p className="text-sm text-gray-500">{resource.description}</p>
                      )}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
