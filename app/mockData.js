// mockData.js
export const mockDatabase = {
  users: [
    {
      userId: "abc123",
      username: "juanperez",
      email: "juan@example.com",
      profilePicture: "https://example.com/juan.jpg",
      createdAt: "2025-04-07T10:00:00Z",
      groups: ["group001"]
    },
    {
      userId: "def456",
      username: "anarodriguez",
      email: "ana@example.com",
      profilePicture: "https://example.com/ana.jpg",
      createdAt: "2025-04-07T11:00:00Z",
      groups: ["group001"]
    }
  ],
  posts: [
    {
      postId: "post001",
      userId: "abc123",
      content: "¡Este post desaparecerá pronto!",
      createdAt: "2025-04-07T12:00:00Z",
      expirationTime: "2025-04-08T12:00:00Z",
      visibility: "public",
      groupId: null,
      eventId: null,
      likes: 3
    },
    {
      postId: "post002",
      userId: "def456",
      content: "Evento esta noche",
      createdAt: "2025-04-07T13:00:00Z",
      expirationTime: "2025-04-08T13:00:00Z",
      visibility: "event",
      groupId: null,
      eventId: "event001",
      likes: 5
    }
  ],
  relationships: [
    {
      relationshipId: "rel001",
      userId: "abc123",
      targetUserId: "def456",
      status: "accepted",
      createdAt: "2025-04-07T14:00:00Z"
    }
  ],
  groups: [
    {
      groupId: "group001",
      name: "Amigos de Viaje",
      description: "Para planear nuestras aventuras",
      creatorId: "abc123",
      createdAt: "2025-04-07T15:00:00Z",
      members: ["abc123", "def456"],
      visibility: "private",
      groupPicture: "https://example.com/group.jpg"
    }
  ],
  events: [
    {
      eventId: "event001",
      name: "Fiesta de Cumpleaños",
      description: "¡Trae tu mejor energía!",
      creatorId: "abc123",
      createdAt: "2025-04-07T15:00:00Z",
      startTime: "2025-04-10T20:00:00Z",
      endTime: "2025-04-11T02:00:00Z",
      location: "Calle Falsa 123",
      participants: ["abc123", "def456"],
      visibility: "private",
      groupId: null,
      eventPicture: "https://example.com/event.jpg"
    }
  ]
};