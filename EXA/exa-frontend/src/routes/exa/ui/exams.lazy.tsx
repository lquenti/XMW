import { createLazyFileRoute } from '@tanstack/react-router'
import { useCourses } from '../../../lib/use-courses'
import { useExams } from '../../../lib/use-exams'
import { parseCoursesXml, parseExamsXml } from '../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/exams')({
  component: RouteComponent,
})

function RouteComponent() {
  const { data, isLoading, error } = useExams()
  const { data: coursesData, isLoading: coursesLoading, error: coursesError } = useCourses()

  if (isLoading || coursesLoading) return <div>Loading...</div>
  if (error || coursesError) return <div>Error: {error?.message || coursesError?.message}</div>
  if (!data || !coursesData) return <div>No data</div>

  const exams = parseExamsXml(data)
  const courses = parseCoursesXml(coursesData)

  // Sort exams by date
  const sortedExams = [...exams]
  sortedExams.sort((a, b) => (a.date ?? '').localeCompare(b.date ?? ''))

  return (
    <div className="p-4">
      <h1>Exams</h1>
      <div className="grid gap-4">
        {sortedExams.map(exam => {
          const course = courses.find(c => c.id === exam.course)
          const examDate = exam.date ? new Date(exam.date) : null

          return (
            <div key={exam.id} className="border p-3 rounded">
              <h3 className="font-semibold">{course?.name ?? 'Unknown Course'}</h3>
              <p>Date: {examDate?.toLocaleString() ?? 'Unknown'}</p>
              <p>Type: {exam.isWritten ? 'Written' : 'Oral'}</p>
              <p>Location: {exam.isOnline ? 'Online' : 'On-site'}</p>
              <p>{exam.roomOrLink}</p>
            </div>
          )
        })}
      </div>
    </div>
  )
}
