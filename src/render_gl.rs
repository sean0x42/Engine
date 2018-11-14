use gl;
use std;
use std::ffi::{CString, CStr};
use resources::{self, Resources};

#[derive(Debug)]
pub enum Error {
    ResourceLoad { name: String, inner: resources::Error },
    CannotDetermineShaderTypeForResource { name: String },
    CompileError { name: String, message: String },
    LinkError { name: String, message: String },
}

pub struct Program {
    gl: gl::Gl,
    id: gl::types::GLuint,
}

impl Program {

    /**
     * Constructs a shader program from a resource.
     *
     * # Arguments
     * - `gl` - The current OpenGL context.
     * - `resource` - Resources folder.
     * - `name` - Name of shader to load.
     */
    pub fn from_resource(gl: &gl::Gl, resource: &Resources, name: &str) -> Result<Program, Error> {
        const POSSIBLE_EXT: [&str; 2] = [".vert", ".frag"];

        // Convert extensions to resource strings
        let resource_names = POSSIBLE_EXT.iter()
            .map(|extension| format!("{}{}", name, extension))
            .collect::<Vec<String>>();

        // Create shaders
        let shaders = resource_names.iter()
            .map(|resource_name| Shader::from_resource(gl, resource, resource_name))
            .collect::<Result<Vec<Shader>, Error>>()?;

        Program::from_shaders(gl, &shaders[..])
            .map_err(|message| Error::LinkError { name: name.into(), message })
    }

    /**
     * Constructs a shader program from an array of Shaders.
     *
     * # Arguments
     * - `gl` - Current OpenGL context.
     * - `shaders` - An array of shaders.
     */ 
    pub fn from_shaders(gl: &gl::Gl, shaders: &[Shader]) -> Result<Program, String> {
        let program_id = unsafe { gl.CreateProgram() };

        for shader in shaders {
            unsafe { gl.AttachShader(program_id, shader.id()); }
        }

        unsafe { gl.LinkProgram(program_id); }

        let mut success: gl::types::GLint = 1;
        unsafe {
            gl.GetProgramiv(program_id, gl::LINK_STATUS, &mut success);
        }

        if success == 0 {
            let mut len: gl::types::GLint = 0;
            unsafe {
                gl.GetProgramiv(program_id, gl::INFO_LOG_LENGTH, &mut len);
            }

            let error = create_whitespace_cstring_with_len(len as usize);

            unsafe {
                gl.GetProgramInfoLog(
                    program_id, len,
                    std::ptr::null_mut(),
                    error.as_ptr() as *mut gl::types::GLchar
                );
            }

            return Err(error.to_string_lossy().into_owned());
        }

        for shader in shaders {
            unsafe { gl.DetachShader(program_id, shader.id()); }
        }

        Ok(Program { gl: gl.clone(), id: program_id })
    }

    pub fn id(&self) -> gl::types::GLuint {
        self.id
    }

    pub fn set_used(&self) {
        unsafe {
            self.gl.UseProgram(self.id);
        }
    }
}

impl Drop for Program {
    fn drop(&mut self) {
        unsafe {
            self.gl.DeleteProgram(self.id);
        }
    }
}

pub struct Shader {
    gl: gl::Gl,
    id: gl::types::GLuint,
}

impl Shader {

    /**
     * Creates a shader from a resource.
     *
     * # Arguments
     * - `gl` - Current OpenGL context.
     * - `resource` - Resource to create shader from.
     * - `name` - Name of shader to create.
     */
    pub fn from_resource(gl: &gl::Gl, resource: &Resources, name: &str) -> Result<Shader, Error> {
        // All extensions and their corresponding GL enum
        const POSSIBLE_EXT: [(&str, gl::types::GLenum); 2] = [
            (".vert", gl::VERTEX_SHADER),
            (".frag", gl::FRAGMENT_SHADER),
        ];

        // Get a list of shader kinds
        let shader_kind = POSSIBLE_EXT.iter()
            .find(|&&(extension, _)| name.ends_with(extension))
            .map(|&(_, kind)| kind)
            .ok_or_else(|| Error::CannotDetermineShaderTypeForResource { name: name.into() })?;

        // Retrieve shader source as c string
        let source = resource.load_cstring(name)
            .map_err(|e| Error::ResourceLoad { name: name.into(), inner: e })?;

        // Construct shader
        Shader::from_source(gl, &source, shader_kind)
            .map_err(|message| Error::CompileError { name: name.into(), message })
    }

    pub fn from_source(gl: &gl::Gl, source: &CStr, kind: gl::types::GLenum) -> Result<Shader, String> {
        let id = shader_from_source(gl, source, kind)?;
        Ok(Shader { gl: gl.clone(), id })
    }

    pub fn from_vert_source(gl: &gl::Gl, source: &CStr) -> Result<Shader, String> {
        Shader::from_source(gl, source, gl::VERTEX_SHADER)
    }

    pub fn from_frag_source(gl: &gl::Gl, source: &CStr) -> Result<Shader, String> {
        Shader::from_source(gl, source, gl::FRAGMENT_SHADER)
    }

    pub fn id(&self) -> gl::types::GLuint {
        self.id
    }
}

impl Drop for Shader {
    fn drop(&mut self) {
        unsafe {
            self.gl.DeleteShader(self.id);
        }
    }
}

fn shader_from_source(
    gl: &gl::Gl,
    source: &CStr, 
    kind: gl::types::GLenum
) -> Result<gl::types::GLuint, String> {
    let id = unsafe { gl.CreateShader(kind) };
    let mut success: gl::types::GLint = 1;

    unsafe {
        gl.ShaderSource(id, 1, &source.as_ptr(), std::ptr::null());
        gl.CompileShader(id);
        gl.GetShaderiv(id, gl::COMPILE_STATUS, &mut success);
    }

    // Handle errors
    if success == 0 {
        let mut len: gl::types::GLint = 0;
        unsafe {
            gl.GetShaderiv(id, gl::INFO_LOG_LENGTH, &mut len);
        }

        let error = create_whitespace_cstring_with_len(len as usize);

        unsafe {
            gl.GetShaderInfoLog(
                id, len,
                std::ptr::null_mut(),
                error.as_ptr() as *mut gl::types::GLchar
            );
        }

        return Err(error.to_string_lossy().into_owned());
    }

    Ok(id)
}

fn create_whitespace_cstring_with_len(len: usize) -> CString {
    // Allocate buffer of correct size
    let mut buffer: Vec<u8> = Vec::with_capacity(len + 1);
    buffer.extend([b' '].iter().cycle().take(len)); // Fill it with len spaces
    unsafe { CString::from_vec_unchecked(buffer) } // Convert buffer to CString
}
