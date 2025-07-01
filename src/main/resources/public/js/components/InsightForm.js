// Современный компонент формы для запроса инсайтов с использованием Shadcn UI
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
    <div className="min-h-screen bg-background p-4">
      <div className="mx-auto max-w-7xl space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="text-3xl font-bold tracking-tight text-foreground md:text-4xl">AI-Insight Dashboard</h1>
        </div>

        {/* Input Section */}
        <div className="mx-auto max-w-2xl">
          <form onSubmit={generateInsights}>
            <div className="flex gap-2">
              <input
                type="text"
                id="topic"
                name="topic"
                value={topic}
                onChange={handleTopicChange}
                placeholder="Введите текст для анализа..."
                className="input flex-1"
                disabled={isLoading}
              />
              <button 
                type="submit" 
                className="button shrink-0 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-2 transition-colors duration-200" 
                disabled={isLoading}
              >
                {isLoading && (
                  <svg className="animate-spin mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                )}
                {isLoading ? 'Генерация...' : 'Сгенерировать инсайты'}
              </button>
            </div>
          </form>
        </div>
        
        {/* Отображение ошибки */}
        {error && (
          <div className="mx-auto max-w-2xl mt-6 p-4 bg-red-50 border border-red-200 rounded">
            <p className="text-red-700">{error}</p>
          </div>
        )}
        
        {/* Отображение результатов или заглушек при загрузке */}
        {(results || isLoading) && (
          <div className="grid gap-6 md:grid-cols-3">
            {/* AI Summary - Takes 2 columns on desktop */}
            <div className="card md:col-span-2">
              <div className="card-header">
                <div className="card-title text-xl">Обзор</div>
              </div>
              <div className="card-content">
                <div className="space-y-4">
                  {isLoading ? (
                    <>
                      <div className="h-4 bg-muted rounded animate-pulse" />
                      <div className="h-4 bg-muted rounded animate-pulse w-4/5" />
                      <div className="h-4 bg-muted rounded animate-pulse w-3/4" />
                      <div className="h-4 bg-muted rounded animate-pulse w-5/6" />
                      <div className="h-4 bg-muted rounded animate-pulse w-2/3" />
                      <div className="space-y-2 pt-4">
                        <div className="h-3 bg-muted rounded animate-pulse" />
                        <div className="h-3 bg-muted rounded animate-pulse w-4/5" />
                        <div className="h-3 bg-muted rounded animate-pulse w-3/4" />
                        <div className="h-3 bg-muted rounded animate-pulse w-5/6" />
                        <div className="h-3 bg-muted rounded animate-pulse w-2/3" />
                      </div>
                    </>
                  ) : (
                    <>
                      <p>{results.overview}</p>
                      {results.applications && (
                        <div className="pt-4">
                          <h4 className="text-lg font-semibold mb-2">Практическое применение</h4>
                          <p>{results.applications}</p>
                        </div>
                      )}
                    </>
                  )}
                </div>
              </div>
            </div>

            {/* Right Column */}
            <div className="space-y-6">
              {/* Key Concepts */}
              <div className="card">
                <div className="card-header">
                  <div className="card-title text-lg">Ключевые понятия</div>
                </div>
                <div className="card-content">
                  <div className="space-y-3">
                    {isLoading ? (
                      <>
                        <div className="flex items-center gap-2">
                          <div className="h-2 w-2 bg-primary rounded-full" />
                          <div className="h-3 bg-muted rounded animate-pulse flex-1" />
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="h-2 w-2 bg-primary rounded-full" />
                          <div className="h-3 bg-muted rounded animate-pulse flex-1 w-4/5" />
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="h-2 w-2 bg-primary rounded-full" />
                          <div className="h-3 bg-muted rounded animate-pulse flex-1 w-3/4" />
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="h-2 w-2 bg-primary rounded-full" />
                          <div className="h-3 bg-muted rounded animate-pulse flex-1 w-5/6" />
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="h-2 w-2 bg-primary rounded-full" />
                          <div className="h-3 bg-muted rounded animate-pulse flex-1 w-2/3" />
                        </div>
                      </>
                    ) : (
                      results.keyConcepts && results.keyConcepts.length > 0 && 
                      results.keyConcepts.map((concept, index) => (
                        <div key={index} className="flex items-center gap-2">
                          <div className="h-2 w-2 bg-primary rounded-full" />
                          <div>{concept}</div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>

              {/* Facts */}
              <div className="card">
                <div className="card-header">
                  <div className="card-title text-lg">Интересные факты</div>
                </div>
                <div className="card-content">
                  <div className="space-y-3">
                    {isLoading ? (
                      <>
                        <div className="space-y-2">
                          <div className="h-3 bg-muted rounded animate-pulse w-4/5" />
                          <div className="h-3 bg-muted rounded animate-pulse w-full" />
                        </div>
                        <div className="space-y-2">
                          <div className="h-3 bg-muted rounded animate-pulse w-full" />
                          <div className="h-3 bg-muted rounded animate-pulse w-3/4" />
                        </div>
                        <div className="space-y-2">
                          <div className="h-3 bg-muted rounded animate-pulse w-5/6" />
                          <div className="h-3 bg-muted rounded animate-pulse w-2/3" />
                        </div>
                      </>
                    ) : (
                      results.facts && results.facts.length > 0 && 
                      results.facts.map((fact, index) => (
                        <div key={index} className="space-y-1">
                          <div className="flex items-center gap-2">
                            <div className="h-2 w-2 bg-primary rounded-full" />
                            <div>{fact}</div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>

              {/* Further Reading */}
              <div className="card">
                <div className="card-header">
                  <div className="card-title text-lg">Полезные ресурсы</div>
                </div>
                <div className="card-content">
                  <div className="space-y-4">
                    {isLoading ? (
                      <>
                        <div className="space-y-2">
                          <div className="h-4 bg-muted rounded animate-pulse w-4/5" />
                          <div className="h-3 bg-muted rounded animate-pulse w-full" />
                        </div>
                        <div className="space-y-2">
                          <div className="h-4 bg-muted rounded animate-pulse w-5/6" />
                          <div className="h-3 bg-muted rounded animate-pulse w-full" />
                        </div>
                        <div className="space-y-2">
                          <div className="h-4 bg-muted rounded animate-pulse w-3/4" />
                          <div className="h-3 bg-muted rounded animate-pulse w-full" />
                        </div>
                      </>
                    ) : (
                      results.relatedLinks && results.relatedLinks.length > 0 &&
                      results.relatedLinks.map((resource, index) => (
                        <div key={index} className="space-y-1">
                          <a 
                            href={resource.url} 
                            target="_blank" 
                            rel="noopener noreferrer"
                            className="text-primary hover:underline font-semibold"
                          >
                            {resource.title}
                          </a>
                          {resource.description && (
                            <p className="text-sm">{resource.description}</p>
                          )}
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
