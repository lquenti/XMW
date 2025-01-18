import { createLazyFileRoute, Link } from '@tanstack/react-router'
import { ExaLogo } from '../../../components/ExaLogo'

export const Route = createLazyFileRoute('/exa/ui/')({
  component: Index,
})

export function Index() {
  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">
          Welcome to <ExaLogo size="lg" />
        </h1>
        <p className="text-xl text-gray-600 max-w-2xl mx-auto">
          Your central hub for exploring courses, lecturers, and exams.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
        <Link
          to="/exa/ui/courses"
          className="group bg-white rounded-xl p-6 shadow-sm hover:shadow-md transition-all border border-gray-200"
        >
          <div className="flex items-center justify-center w-12 h-12 bg-blue-100 rounded-lg mb-4 group-hover:scale-110 transition-transform">
            <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Courses</h2>
          <p className="text-gray-600">Browse and manage course offerings across all faculties.</p>
        </Link>

        <Link
          to="/exa/ui/lecturers"
          className="group bg-white rounded-xl p-6 shadow-sm hover:shadow-md transition-all border border-gray-200"
        >
          <div className="flex items-center justify-center w-12 h-12 bg-green-100 rounded-lg mb-4 group-hover:scale-110 transition-transform">
            <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Lecturers</h2>
          <p className="text-gray-600">Find information about our teaching staff and faculty members.</p>
        </Link>

        <Link
          to="/exa/ui/exams"
          className="group bg-white rounded-xl p-6 shadow-sm hover:shadow-md transition-all border border-gray-200"
        >
          <div className="flex items-center justify-center w-12 h-12 bg-purple-100 rounded-lg mb-4 group-hover:scale-110 transition-transform">
            <svg className="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
            </svg>
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Exams</h2>
          <p className="text-gray-600">View and manage upcoming and past examination schedules.</p>
        </Link>
      </div>

      <div className="bg-blue-50 rounded-xl p-8 border border-blue-100">
        <div className="flex items-start space-x-4">
          <div className="flex-shrink-0">
            <svg className="w-8 h-8 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-2">Getting Started</h2>
            <p className="text-gray-600 mb-4">
              Select any of the sections above to start exploring the EXA system. Each section provides detailed information and management capabilities for their respective areas.
            </p>
            <p className="text-gray-600">
              Need help? Contact the system administrator or refer to the user documentation.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
