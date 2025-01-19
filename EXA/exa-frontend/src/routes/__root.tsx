import { createRootRoute, Link, Outlet } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/router-devtools'
import { Toaster } from 'sonner'
import { ExaLogo } from '../components/ExaLogo'
import { AuthorizationState, useAuthorizationState } from '../lib/utils'

// Define the states array to cycle through
const states = [
  AuthorizationState.Guest,
  AuthorizationState.Student,
  AuthorizationState.Lecturer,
  AuthorizationState.Admin,
] as const

export const Route = createRootRoute({
  component: LayoutComponent,
})

function LayoutComponent() {
  const { state, setState } = useAuthorizationState()

  const cycleState = () => {
    const currentIndex = states.indexOf(state)
    const nextIndex = (currentIndex + 1) % states.length
    setState(states[nextIndex])
  }

  return (
    <div className="min-h-screen font-sans flex flex-col bg-gray-50">
      <nav className="bg-white border-b border-gray-200 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <ul className="flex flex-row gap-6">
              <li>
                <Link
                  to="/exa/ui"
                  className="hover:opacity-75 transition-opacity [&.active]:italic"
                >
                  <ExaLogo />
                </Link>
              </li>
              <li>
                <Link
                  to="/exa/ui/courses"
                  className="text-gray-600 hover:text-gray-900 transition-colors [&.active]:text-blue-600 [&.active]:font-semibold [&.active]:italic"
                >
                  Courses
                </Link>
              </li>
              <li>
                <Link
                  to="/exa/ui/lecturers"
                  className="text-gray-600 hover:text-gray-900 transition-colors [&.active]:text-blue-600 [&.active]:font-semibold [&.active]:italic"
                >
                  Lecturers
                </Link>
              </li>
              <li>
                <Link
                  to="/exa/ui/exams"
                  className="text-gray-600 hover:text-gray-900 transition-colors [&.active]:text-blue-600 [&.active]:font-semibold [&.active]:italic"
                >
                  Exams
                </Link>
              </li>
            </ul>
            <div className="flex items-center">
              <button
                onClick={cycleState}
                className="flex items-center space-x-2 text-sm text-gray-600 hover:text-gray-900 transition-colors group"
              >
                <svg
                  className="w-5 h-5 group-hover:scale-110 transition-transform"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                <span>Logged in as <span className="font-medium text-gray-900">{state}</span></span>
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="flex-1">
        <Outlet />
      </main>

      <footer className="bg-white border-t border-gray-200 mt-auto">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-6">
              <span className="text-gray-600">Other services:</span>
              <ul className="flex gap-6">
                <li>
                  <a
                    href="/studip"
                    className="text-gray-600 hover:text-gray-900 transition-colors"
                  >
                    Stud.IP
                  </a>
                </li>
                <li>
                  <a
                    href="/user"
                    className="text-gray-600 hover:text-gray-900 transition-colors"
                  >
                    User API
                  </a>
                </li>
                <li>
                  <a
                    href="/logger"
                    className="text-gray-600 hover:text-gray-900 transition-colors"
                  >
                    Logger
                  </a>
                </li>

              </ul>
            </div>
            <div className="text-sm text-gray-500">
              © {new Date().getFullYear()} EXA System
            </div>
          </div>
        </div>
      </footer>
      <Toaster />
      {import.meta.env.DEV && <TanStackRouterDevtools />}
    </div>
  )
}
