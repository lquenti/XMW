import { createRootRoute, Link, Outlet } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/router-devtools'
import { useAuthorizationState } from '../lib/utils'

export const Route = createRootRoute({
  component: LayoutComponent,
})

function LayoutComponent() {
  const authorizationState = useAuthorizationState()
  return (
    <div className="container flex flex-col min-h-screen font-sans">
      <nav className="w-1/2 mx-auto">
        <div className="flex justify-between items-center">
          <ul className="flex flex-row gap-4">
            <li>
              <Link to="/exa/ui" className="[&.active]:font-bold">
                Exa
              </Link>
            </li>
            <li>
              <Link to="/exa/ui/courses" className="[&.active]:font-bold">
                Courses
              </Link>
            </li>
            <li>
              <Link to="/exa/ui/lecturers" className="[&.active]:font-bold">
                Lecturers
              </Link>
            </li>
            <li>
              <Link to="/exa/ui/exams" className="[&.active]:font-bold">
                Exams
              </Link>
            </li>
          </ul>
          <div>
            <p>
              You are currently logged in as {authorizationState.state}.
            </p>
          </div>
        </div>
      </nav>
      <hr />
      <main className="flex-1">
        <Outlet />
      </main>
      <footer className="mt-auto">
        <hr />
        <div className="w-1/2 mx-auto">
          <div className="flex items-center gap-4">
            <p>Other services:</p>
            <ul className="flex gap-4">
              <li>
                <a href="/studip">Stud.IP</a>
              </li>
              <li>
                <a href="/user">User API</a>
              </li>
            </ul>
          </div>
        </div>
      </footer>
      <TanStackRouterDevtools />
    </div>
  )
}
