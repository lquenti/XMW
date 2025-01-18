import { createLazyFileRoute, Link } from '@tanstack/react-router'
import { useCourses } from '../../../../lib/use-courses'
import { useExams } from '../../../../lib/use-exams'
import { useLecturers } from '../../../../lib/use-lecturers'
import { useLectures } from '../../../../lib/use-lectures'
import { parseCoursesXml, parseExamsXml, parseLecturersXml, parseLecturesXml } from '../../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/courses/$courseId')({
    component: CourseDetailComponent,
})

function CourseDetailComponent() {
    const { courseId } = Route.useParams()
    const { data: coursesData, isLoading: coursesLoading } = useCourses()
    const { data: lecturersData, isLoading: lecturersLoading } = useLecturers()
    const { data: examsData, isLoading: examsLoading } = useExams()
    const { data: lecturesData, isLoading: lecturesLoading } = useLectures()

    if (coursesLoading || lecturersLoading || examsLoading || lecturesLoading) {
        return (
            <div className="flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        )
    }

    if (!coursesData || !lecturersData || !examsData || !lecturesData) {
        return (
            <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
                No data available
            </div>
        )
    }

    const courses = parseCoursesXml(coursesData)
    const lecturers = parseLecturersXml(lecturersData)
    const exams = parseExamsXml(examsData)
    const lectures = parseLecturesXml(lecturesData)

    const course = courses.find(c => c.id === courseId)
    const lecturer = lecturers.find(l => l.id === course?.lecturer)
    const courseExams = exams.filter(e => e.course === courseId)
    const courseLectures = lectures.filter(l => l.course === courseId)
        .sort((a, b) => (a.start ?? '').localeCompare(b.start ?? ''))

    // Debug logging
    console.log('Raw Lectures Data:', lectures.filter(l => l.course === courseId))
    console.log('Raw Exams Data:', exams.filter(e => e.course === courseId))

    // Helper function to parse date strings
    const parseDate = (dateStr: string | null): Date | null => {
        if (!dateStr) return null
        try {
            // First try parsing as a normal ISO date
            const normalDate = new Date(dateStr)
            if (!isNaN(normalDate.getTime())) {
                return normalDate
            }

            // If that fails, try handling the format with extra T
            const match = dateStr.match(/^(\d{4}-\d{2}-\d{2})T(\d+)T:(\d{2}):(\d{2})$/)
            if (!match) return null

            const [, datePart, hours, minutes, seconds] = match
            // Pad hours with leading zero if needed
            const paddedHours = hours.padStart(2, '0')
            const timeStr = `${paddedHours}:${minutes}:${seconds}`

            const cleanDate = `${datePart} ${timeStr}`
            const date = new Date(cleanDate)
            return isNaN(date.getTime()) ? null : date
        } catch (error) {
            console.error('Error parsing date:', error)
            return null
        }
    }

    // Helper function to format dates
    const formatDateTime = (date: Date | null): string => {
        if (!date) return 'Date not set'
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit'
        })
    }

    // Helper function to format time only
    const formatTime = (date: Date | null): string => {
        if (!date) return 'Time not set'
        return date.toLocaleString('en-US', {
            hour: 'numeric',
            minute: '2-digit'
        })
    }

    if (!course) {
        return (
            <div className="p-4 bg-red-50 text-red-600 rounded-lg">
                Course not found
            </div>
        )
    }

    return (
        <div className="max-w-7xl mx-auto p-6">
            <div className="mb-8">
                <Link
                    to="/exa/ui/courses"
                    className="text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2 mb-4"
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    Back to Courses
                </Link>

                <div className="bg-white rounded-xl p-8 shadow-sm">
                    <div className="flex justify-between items-start mb-6">
                        <h1 className="text-3xl font-bold text-gray-900">{course.name}</h1>
                        <span className="px-4 py-2 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                            {course.faculty}
                        </span>
                    </div>

                    <div className="grid grid-cols-1 gap-8">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                            <div>
                                <h2 className="text-xl font-semibold text-gray-900 mb-4">Course Details</h2>
                                <div className="space-y-4">
                                    <div className="flex items-center text-gray-600">
                                        <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                                        </svg>
                                        Max Students: {course.maxStudents}
                                    </div>
                                </div>
                            </div>

                            {lecturer && (
                                <div>
                                    <h2 className="text-xl font-semibold text-gray-900 mb-4">Lecturer</h2>
                                    <div className="bg-gray-50 rounded-lg p-6 border border-gray-200">
                                        <div className="flex items-start space-x-4">
                                            <img
                                                src={`https://i.pravatar.cc/150?u=${lecturer.id}`}
                                                alt={`${lecturer.firstname} ${lecturer.name}`}
                                                className="w-16 h-16 rounded-full object-cover"
                                            />
                                            <div>
                                                <h3 className="text-xl font-semibold text-gray-900">
                                                    {lecturer.firstname} {lecturer.name}
                                                </h3>
                                                <div className="mt-2 space-y-2">
                                                    <div className="flex items-center text-gray-600">
                                                        <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                                                        </svg>
                                                        {lecturer.faculty ?? 'No Faculty'}
                                                    </div>
                                                    <div className="flex items-center text-gray-600">
                                                        <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                                                        </svg>
                                                        {lecturer.username}
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>

                        <div>
                            <h2 className="text-xl font-semibold text-gray-900 mb-4">Exams</h2>
                            {courseExams.length > 0 ? (
                                <div className="space-y-4">
                                    {courseExams.map(exam => {
                                        const examDate = parseDate(exam.date)
                                        const isUpcoming = examDate ? examDate >= new Date() : false

                                        return (
                                            <Link
                                                key={exam.id}
                                                to="/exa/ui/exams/$examId"
                                                params={{ examId: exam.id ?? '' }}
                                                className={`block p-4 rounded-lg border ${isUpcoming ? 'bg-white border-blue-200 hover:border-blue-300' : 'bg-gray-50 border-gray-200 hover:border-gray-300'} transition-colors`}
                                            >
                                                <div className="flex justify-between items-start mb-2">
                                                    <span className={`px-3 py-1 rounded-full text-sm ${exam.isWritten ? 'bg-purple-100 text-purple-700' : 'bg-green-100 text-green-700'}`}>
                                                        {exam.isWritten ? 'Written' : 'Oral'}
                                                    </span>
                                                    <span className={`text-sm ${isUpcoming ? 'text-blue-600' : 'text-gray-500'}`}>
                                                        {formatDateTime(examDate)}
                                                    </span>
                                                </div>
                                                <div className="text-gray-600 mt-2">
                                                    {exam.isOnline ? 'Online' : 'On-site'} - {exam.roomOrLink}
                                                </div>
                                            </Link>
                                        )
                                    })}
                                </div>
                            ) : (
                                <p className="text-gray-500">No exams scheduled</p>
                            )}
                        </div>

                        <div>
                            <h2 className="text-xl font-semibold text-gray-900 mb-4">Lectures</h2>
                            {courseLectures.length > 0 ? (
                                <div className="space-y-4">
                                    {courseLectures.map(lecture => {
                                        const startDate = parseDate(lecture.start)
                                        const endDate = parseDate(lecture.end)
                                        const isUpcoming = startDate ? startDate >= new Date() : false

                                        return (
                                            <div
                                                key={lecture.id}
                                                className={`p-4 rounded-lg border ${isUpcoming ? 'bg-white border-blue-200' : 'bg-gray-50 border-gray-200'}`}
                                            >
                                                <div className="flex justify-between items-start mb-2">
                                                    <div className="space-y-1">
                                                        <div className={`text-sm ${isUpcoming ? 'text-blue-600' : 'text-gray-500'}`}>
                                                            {formatDateTime(startDate)} - {formatTime(endDate)}
                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="flex items-center text-gray-600 mt-2">
                                                    <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                                                    </svg>
                                                    {lecture.roomOrLink}
                                                </div>
                                            </div>
                                        )
                                    })}
                                </div>
                            ) : (
                                <p className="text-gray-500">No lectures scheduled</p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
} 