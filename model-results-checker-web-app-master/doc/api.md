# Server Rest API calls

All calls are made using http GET requests. During development, the API is accessed by `http://localhost:8080/`
To pass parameters in a Post request, send a dto from the client side. This will convert the object into parameters
which the server will interpret.

## User

### Get Login Info

`/api/get/userInfo`

Request:

| Type Parameter    | Description    |
|--------	|----------------------	|
| `Ooid`    | User Id    |
| `BearerToken`    | Authentication Token    |

Response:

| Type    | Description    |
|--------	|----------------------	|
| `AdminDto`    | Registered admin with oid, username and email    |
| `UserDto`    | Registered basic user with oid, username and email    |

### Get Admin(Need admin rights)

`/api/get/admin`

Request:

| Type    | Description    |
|--------	|----------------------	|
| `UserDto`    | Contains admin with Oid    |

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully sent    |
| 400    | Malformed request    |
| 500    | User does not exist    |

### Update Admin(Need admin rights)

`/api/get/admin`

Request:

| Type    | Description    |
|--------	|----------------------	|
| `UserDto`    | Contains admin with Oid    |

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully sent    |
| 400    | Malformed request    |
| 500    | User does not exist    |

### Delete Admin(Need admin rights)

`/api/get/admin`

Request:

| Type    | Description    |
|--------	|----------------------	|
| `UserDto`    | Contains admin with Oid    |

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully sent    |
| 400    | Malformed request    |
| 500    | User does not exist    |

### List Admin(Need admin rights)

`/api/list/admin`

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully sent    |

### Get User(Need admin rights)

`/api/get/user`

Request:

| Type    | Description    |
|--------	|----------------------	|
| `UserDto`    | Contains user with Oid    |

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully sent    |
| 400    | Malformed request    |
| 500    | User does not exist    |

### Update User(Need admin rights)

`/api/update/user`

Request:

Note: You can only update username

| Type    | Description    |
|--------	|----------------------	|
| `UserDto`    | Contains user with Oid, username    |

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully updated    |
| 400    | Malformed request    |
| 500    | User does not exist    |

### Remove User(Need admin rights)

`/api/delete/user`

Request:

| Type    | Description    |
|--------	|----------------------	|
| `UserDto`    | Contains user with Oid    |

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully deleted    |
| 400    | Malformed request    |
| 500    | User does not exist    |

### List User(Need admin rights)

`/api/list/user`

Response:

| HTTP code    | Description    |
|-----------	|-------------	|
| 200    | Status OK, user has been successfully sent    |

## Project Holder (Admin only)

### Create

`/api/create/projectHolder`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectHolderDto`    | Contains project holder with id, adminId, userId, and Set<ProjectDto> projects|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, project holder has been successfully created    |
|400| Malform request|
|500| ExistsException if project holder already exists, NotFoundException if admin does not exist, AuthorityException if not admin|

### Assign a client to a project holder

`/api/client/projectholder`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectHolderDto`    | Contains project holder with id, adminId, userId, and Set<ProjectDto> projects|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, project holder has been successfully assigned    |
|400| Malform request|
|500| NotFoundException if client does not exist, AuthorityException if normal user try to assign projects|

### Delete

`/api/delete/projectholder`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectHolderDto`    | Contains project holder with id, adminId, userId, and Set<ProjectDto> projects|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, project holder has been successfully deleted    |
|400| Malform request|
|500| NotFoundException if project holder does not exist|

### Get projects in project holder

`/api/projects/projectholder`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectHolderDto`    | Contains project holder with id, adminId, userId, and Set<ProjectDto> projects|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, projects has been successfully retrieved    |
|400| Malform request|
|500| NotFoundException if project holder does not exist or list of projects is null|

### List all project holders

`/api/list/projectholder`

Request

No parameter required

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, project holders has been successfully retrieved    |
|400| Malform request|
|500| NotFoundException if there's no project holder|

### Get project holder

`/api/get/projectholder`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectHolderDto`    | Contains project holder with id, adminId, userId, and Set<ProjectDto> projects|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, projectholder has been successfully retrieved    |
|400| Malform request|
|500| NotFoundException if project holder does not exist|

## Project (Admin Only)

### Get Project

`/api/get/project`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectDto`    | Contains project with id, title, description, prjectHolderid and Set<ContainerDto> containerDtoSet|

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, project has been successfully retrieved    |
|400| Malformed request|
|500| NotFoundException if project does not exist|

### List Projects

`/api/list/project`
Note: this endpoint is for admin only

Request

No parameter required

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, all projects has been successfully retrieved    |
|400| Malformed request|
|500| NotFoundException when there's no projects in db|

### Create Project

`/api/create/project`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectDto`    | Contains project with id, title, description, prjectHolderid and Set<ContainerDto> containerDtoSet|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, project has been successfully created    |
|400| Malform request|
|500| NotFoundException if project holder does not exist and ExistsException if project already exists |

### Update project

`/api/update/project`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectDto`    | Contains project with id, title, description, prjectHolderid and Set<ContainerDto> containerDtoSet|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, project has been successfully updated    |
|400| Malform request|
|500|NotFoundException NotFoundException if project does not exist|

### Delete Project

`/api/delete/project`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectDto`    | Contains project with id, title, description, prjectHolderid and Set<ContainerDto> containerDtoSet|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, project has been successfully deleted    |
|400| Malform request|
|500| NotFoundException if project does not exist|

### Get containers in a project

`/api/containers/project`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ProjectDto`    | Contains project with id, title, description, prjectHolderid and Set<ContainerDto> containerDtoSet|

Response:

| Type    | Description    |
|--------	|----------------------	|
| 200    | Status OK, containers have been successfully retrieved    |
|400| Malform request|
|500| NotFoundException if project doesn't have any containers|

## Container

### Create

`/api/create/container`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ContainerDto` | container contains id, blobId, csvId, projectId, submissionId, frameRate, name and description |

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, container has been successfully created    |
|400| Malformed request|
|500| if container already exist or the project does not exist|

### Update container

`/api/update/container`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ContainerDto` | container contains id, blobId, csvId, projectId, submissionId, frameRate, name and description |

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, container has been successfully updated    |
|400| Malformed request|
|500| if container does not exist|

### Get container

`/api/get/container`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ContainerDto` | container contains id, blobId, csvId, projectId, submissionId, frameRate, name and description |

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, container has been successfully retrieved    |
|400| Malformed request|
|500| if container does not exist|

### Delete container

`/api/delete/container`

Request

| Type    | Description    |
|--------	|----------------------	|
| `ContainerDto` | container contains id, blobId, csvId, projectId, submissionId, frameRate, name and description |

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, container has been successfully deleted    |
|400| Malformed request|
|500| if container does not exist|

### List all containers

`/api/list/container`

Request

No params required

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, containers has been successfully retrieved    |
|400| Malformed request|
|500| if there's no containers|

## CSV

### Get Csv

`/api/get/csv`

Request

| Type    | Description    |
|--------	|----------------------	|
| `CSvDto` | csv contains id |

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, csv has been successfully retrieved    |
|400| Malformed request|
|500| if csv does not exist|

### delete Csv

`/api/delete/csv`

Request

| Type    | Description    |
|--------	|----------------------	|
| `CSvDto` | csv contains id |

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, csv has been successfully deleted    |
|400| Malformed request|
|500| if csv does not exist|

### list all Csvs

`/api/list/csv`

Request

no parameter required

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, csvs has been successfully retrieved    |
|400| Malformed request|
|500| if there's no csv|

### upload Csv

`/api/upload/csv`

Request

| Type    | Description    |
|--------	|----------------------	|
| `file` | MultipartFile |
|`containerId`|Long|

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, csv has been successfully uploaded    |
|400| Malformed request|
|500| if csv already exist or container does not exist or IOException or constraint violations|

### Get records of a Csv

`/api/records/csv`

Request

| Type    | Description    |
|--------	|----------------------	|
| `CSvDto` | csv contains id |

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, records has been successfully retrieved    |
|400| Malformed request|
|500| if csv does not exist or there's no records in the csv|

## Blob

### Create

`/api/create/blob`

Request

| Type    | Description    |
|--------	|----------------------	|
| `file` | MultipartFile|
|`containerId`| Long|

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, blob has been successfully created    |
|400| Malformed request|
|500| if container does not exist, or video already exist or IOException or constraint violations|

### Get blob

`/api/get/blob`

Request

| Type    | Description    |
|--------	|----------------------	|
| `BlobDto` | Blob contains id, byte[] video and containerId|

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, blob has been successfully retrieved    |
|400| Malformed request|
|500| if video does not exist|

### Delete blob

`/api/delete/blob`

Request

| Type    | Description    |
|--------	|----------------------	|
| `BlobDto` | Blob contains id, byte[] video and containerId|

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, blob has been successfully deleted    |
|400| Malformed request|
|500| if video does not exist|

### List all blobs

`/api/list/blob`

Request

No parameter required

Response:

| Type    | Description    |
|--------	|----------------------	|
|  200    | Status Ok, blobs has been successfully retrieved    |
|400| Malformed request|
|500| if there's no video|

