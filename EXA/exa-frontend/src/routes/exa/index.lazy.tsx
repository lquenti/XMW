import { createLazyFileRoute } from '@tanstack/react-router'
import { Index } from './ui/index.lazy'

export const Route = createLazyFileRoute('/exa/')({
    component: Index,
})


