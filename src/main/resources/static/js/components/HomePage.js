// HomePage component
const HomePage = () => {
  // We're adapting the provided component to our CDN-based React setup
  // Original imports were:
  // import { Button } from "@/components/ui/button"
  // import { Input } from "@/components/ui/input"
  // import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
  // import { Search } from "lucide-react"
  
  // Simple Search icon component
  const Search = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <circle cx="11" cy="11" r="8"></circle>
      <path d="m21 21-4.3-4.3"></path>
    </svg>
  );
  
  return (
    <div className="min-h-screen bg-background p-4">
      <div className="mx-auto max-w-7xl space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="text-3xl font-bold tracking-tight text-foreground md:text-4xl">AI-Insight Dashboard</h1>
        </div>

        {/* Input Section */}
        <div className="mx-auto max-w-2xl">
          <div className="flex gap-2">
            <input placeholder="Введите текст для анализа..." className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
            <button className="shrink-0 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-2 rounded-md transition-colors duration-200 flex items-center">
              <Search className="mr-2 h-4 w-4" />
              Сгенерировать инсайты
            </button>
          </div>
        </div>

        {/* Content Grid */}
        <div className="grid gap-6 md:grid-cols-3">
          {/* AI Summary - Takes 2 columns on desktop */}
          <div className="md:col-span-2 bg-white rounded-lg shadow-md overflow-hidden">
            <div className="p-4 border-b">
              <h3 className="text-xl font-semibold">AI Summary</h3>
            </div>
            <div className="p-4">
              <div className="space-y-4">
                <div className="h-4 bg-gray-200 rounded animate-pulse" />
                <div className="h-4 bg-gray-200 rounded animate-pulse w-4/5" />
                <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4" />
                <div className="h-4 bg-gray-200 rounded animate-pulse w-5/6" />
                <div className="h-4 bg-gray-200 rounded animate-pulse w-2/3" />
                <div className="space-y-2 pt-4">
                  <div className="h-3 bg-gray-200 rounded animate-pulse" />
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-4/5" />
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-3/4" />
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-5/6" />
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-2/3" />
                </div>
              </div>
            </div>
          </div>

          {/* Right Column */}
          <div className="space-y-6">
            {/* Key Concepts */}
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
              <div className="p-4 border-b">
                <h3 className="text-lg font-semibold">Key Concepts</h3>
              </div>
              <div className="p-4">
                <div className="space-y-3">
                  <div className="flex items-center gap-2">
                    <div className="h-2 w-2 bg-blue-600 rounded-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse flex-1" />
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="h-2 w-2 bg-blue-600 rounded-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse flex-1 w-4/5" />
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="h-2 w-2 bg-blue-600 rounded-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse flex-1 w-3/4" />
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="h-2 w-2 bg-blue-600 rounded-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse flex-1 w-5/6" />
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="h-2 w-2 bg-blue-600 rounded-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse flex-1 w-2/3" />
                  </div>
                </div>
              </div>
            </div>

            {/* Further Reading */}
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
              <div className="p-4 border-b">
                <h3 className="text-lg font-semibold">Further Reading</h3>
              </div>
              <div className="p-4">
                <div className="space-y-4">
                  <div className="space-y-2">
                    <div className="h-4 bg-gray-200 rounded animate-pulse w-4/5" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-3/4" />
                  </div>
                  <div className="space-y-2">
                    <div className="h-4 bg-gray-200 rounded animate-pulse w-5/6" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-2/3" />
                  </div>
                  <div className="space-y-2">
                    <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-full" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-4/5" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
