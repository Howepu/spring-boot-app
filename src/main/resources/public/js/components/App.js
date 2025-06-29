// Основной компонент приложения
function App() {
  return (
    <div className="container mx-auto px-4 py-8">
      <header className="mb-8 text-center">
        <h1 className="text-3xl font-bold text-primary">AI-Insight Dashboard</h1>
        <p className="text-gray-600 mt-2">Получите глубокий анализ любой темы с помощью AI</p>
      </header>
      
      <main>
        <InsightForm />
      </main>
      
      <footer className="mt-12 text-center text-sm text-gray-500">
        <p>© 2025 AI-Insight Dashboard. Все права защищены.</p>
      </footer>
    </div>
  );
}
