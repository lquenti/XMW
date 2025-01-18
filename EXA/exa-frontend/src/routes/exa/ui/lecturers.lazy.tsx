import { createLazyFileRoute } from '@tanstack/react-router'
import { useLecturers } from '../../../lib/use-lecturers'
import { parseLecturersXml } from '../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/lecturers')({
  component: RouteComponent,
})

function RouteComponent() {
  const { data, isLoading, error } = useLecturers()

  if (isLoading) return <div>Loading...</div>
  if (error) return <div>Error: {error.message}</div>
  if (!data) return <div>No data</div>

  const lecturers = parseLecturersXml(data)

  // Group lecturers by faculty
  const lecturersByFaculty = lecturers.reduce<Record<string, typeof lecturers>>((acc, lecturer) => {
    const faculty = lecturer.faculty ?? 'Other'
    acc[faculty] = acc[faculty] || []
    acc[faculty].push(lecturer)
    return acc
  }, {})

  return (
    <div className="p-4">
      <h1>Lecturers</h1>
      {Object.entries(lecturersByFaculty).map(([faculty, facultyLecturers]) => (
        <div key={faculty} className="mb-6">
          <h2 className="text-xl font-bold mb-3">{faculty}</h2>
          <div className="grid gap-4">
            {facultyLecturers.map(lecturer => (
              <div key={lecturer.id} className="border p-3 rounded">
                <h3 className="font-semibold">{lecturer.firstname} {lecturer.name}</h3>
                <p>Username: {lecturer.username}</p>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  )
}
