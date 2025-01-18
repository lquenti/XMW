import { createLazyFileRoute } from '@tanstack/react-router'

export const Route = createLazyFileRoute('/exa/ui/')({
  component: Index,
})

export function Index() {
  return (
    <div className="p-2">
      <h1 className="text-2xl font-bold">Welcome to Exa!</h1>
      <p>Chose a section from the navigation bar to get started.</p>
    </div>
  )
}
